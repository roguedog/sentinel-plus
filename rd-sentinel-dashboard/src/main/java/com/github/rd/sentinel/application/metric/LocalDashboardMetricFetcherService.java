
package com.github.rd.sentinel.application.metric;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.node.metric.MetricNode;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.rd.sentinel.application.entity.MetricEntity;
import com.github.rd.sentinel.infrastructure.config.LocalMetricProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Fetch metric of local.
 *
 * 
 */
@Component
public class LocalDashboardMetricFetcherService {

    @Resource
    private LocalMetricSearcher localMetricSearcher;

    private long fetchIntervalMillis;

    public static final String NO_METRICS = "No metrics";
    private static Logger logger = LoggerFactory.getLogger(LocalDashboardMetricFetcherService.class);
    private String appName = "sentinel-dashboard";

    private Map<String, AtomicLong> appLastFetchTime = new ConcurrentHashMap<>();
    private AtomicLong lastFetchTime = new AtomicLong(System.currentTimeMillis());

    @SuppressWarnings("PMD.ThreadPoolCreationRule")
    private ExecutorService fetchWorker;

    @Autowired
    public LocalDashboardMetricFetcherService(LocalMetricProperties localMetricProperties) {
        this.fetchIntervalMillis = localMetricProperties.getFetchIntervalSeconds() * 1000;
        int corePoolSize = 1;
        int maxPoolSize = 5;
        long keepAliveTime = 0;
        int queueSize = 50;
        RejectedExecutionHandler handler = new DiscardPolicy();
        fetchWorker = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("lmw"), handler);
    }

    public void fetch() {
        long startTime = lastFetchTime.get();
        long endTime = startTime + fetchIntervalMillis;
        long now = System.currentTimeMillis();

        if (endTime > now - 2000) {
            System.out.println("结束时间与当前时间接近,放弃执行");
            return;
        }

        System.out.println("时间间隔"+(endTime - startTime));
        System.out.println("$与当前时间的差距"+(now - endTime));
        // update last_fetch in advance.
        lastFetchTime.set(endTime);
        final long finalStartTime = startTime;
        final long finalEndTime = endTime;
        try {
            // do real fetch async
            fetchWorker.submit(() -> {
                try {
                    fetchOnce(finalStartTime, finalEndTime);
                } catch (Exception e) {
                    logger.info("fetchOnce(" + appName + ") error", e);
                }
            });
        } catch (Exception e) {
            logger.info("submit fetchOnce(" + appName + ") fail, intervalMs [" + startTime + ", " + endTime + "]", e);
        }
    }

    private void writeMetric(Map<String, MetricEntity> map) {
        if (map.isEmpty()) {
            return;
        }
        Date date = new Date();
        for (MetricEntity entity : map.values()) {
            entity.setGmtCreate(date);
            entity.setGmtModified(date);
        }
        //  todo 持久化
        System.out.println("todo 拉取本地指标，待持久化"+JSONObject.toJSON(map));
    }

    private void fetchOnce(long startTime, long endTime) {
        //查询所有资源的指标
        String response = localMetricSearcher.search(startTime, endTime, null);
        Map<String, MetricEntity> metricEntityMap = parseMetric(response);
        writeMetric(metricEntityMap);
    }

    private Map<String, MetricEntity> parseMetric(String metricStr) {
        if (StringUtil.isEmpty(metricStr) || metricStr.startsWith(NO_METRICS)) {
            return null;
        }
        String[] lines = metricStr.split("\n");
        if (lines.length < 1) {
            return null;
        }

        final Map<String, MetricEntity> metricMap = new ConcurrentHashMap<>(16);
        for (String line : lines) {
            try {
                MetricNode node = MetricNode.fromThinString(line);
                if (shouldFilterOut(node.getResource())) {
                    continue;
                }

                String key = buildMetricKey(appName, node.getResource(), node.getTimestamp());

                MetricEntity metricEntity = metricMap.computeIfAbsent(key, s -> {
                    MetricEntity initMetricEntity = new MetricEntity();
                    initMetricEntity.setApp(appName);
                    initMetricEntity.setTimestamp(new Date(node.getTimestamp()));
                    initMetricEntity.setPassQps(0L);
                    initMetricEntity.setBlockQps(0L);
                    initMetricEntity.setRtAndSuccessQps(0, 0L);
                    initMetricEntity.setExceptionQps(0L);
                    initMetricEntity.setCount(0);
                    initMetricEntity.setResource(node.getResource());
                    return initMetricEntity;
                });
                metricEntity.addPassQps(node.getPassQps());
                metricEntity.addBlockQps(node.getBlockQps());
                metricEntity.addRtAndSuccessQps(node.getRt(), node.getSuccessQps());
                metricEntity.addExceptionQps(node.getExceptionQps());
                metricEntity.addCount(1);
            } catch (Exception e) {
                logger.warn("handle metric exception, metric: {}", line);
            }
        }
        return metricMap;
    }

    private String buildMetricKey(String app, String resource, long timestamp) {
        return app + "__" + resource + "__" + (timestamp / 1000);
    }

    private boolean shouldFilterOut(String resource) {
        return RES_EXCLUSION_SET.contains(resource);
    }

    private static final Set<String> RES_EXCLUSION_SET = new HashSet<String>() {{
       add(Constants.TOTAL_IN_RESOURCE_NAME);
       add(Constants.SYSTEM_LOAD_RESOURCE_NAME);
       add(Constants.CPU_USAGE_RESOURCE_NAME);
    }};

}



