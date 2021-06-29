package com.github.rd.sentinel.dashboard.application.resource;

import com.alibaba.csp.sentinel.Constants;
import com.alibaba.csp.sentinel.adapter.spring.webflux.SentinelWebFluxFilter;
import com.alibaba.csp.sentinel.adapter.spring.webflux.exception.SentinelBlockExceptionHandler;
import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.node.metric.MetricNode;
import com.alibaba.csp.sentinel.node.metric.MetricSearcher;
import com.alibaba.csp.sentinel.node.metric.MetricWriter;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.util.PidUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.util.TimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.rd.sentinel.dashboard.application.entity.MetricEntity;
import com.github.rd.sentinel.dashboard.entrypoint.common.LocalBashboardMetricEntryPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
@ConditionalOnBean({LocalBashboardMetricEntryPoint.LocalDashboardMetricFetchSchedule.class})
public class DashboardResourceManager {
    private ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1, new NamedThreadFactory("lms"));
    private LocalDashboardMetricFetcherService localDashboardMetricFetcherService;
    private long fetchLocalMetricPeriodSchedulerSeconds;

    @Autowired
    public DashboardResourceManager(DashboardResourceManagerProperties dashboardResourceManagerProperties) {
        this.localDashboardMetricFetcherService = new LocalDashboardMetricFetcherService(dashboardResourceManagerProperties);
        this.fetchLocalMetricPeriodSchedulerSeconds = dashboardResourceManagerProperties.getFetchMetricIntervalSeconds() - 1;
    }

    public void startFetchSchedule() {
        scheduleService.scheduleAtFixedRate(() -> {
            try {
                localDashboardMetricFetcherService.fetch();
            } catch (Exception e) {
                log.info("fetchLocalMetric error:", e);
            }
        }, 3, fetchLocalMetricPeriodSchedulerSeconds, TimeUnit.SECONDS);
        log.info("Local metric fetch schedule init");
    }


    /**
     * API限流配置
     */
    @Configuration
    public class WebFluxSentinelConfig {

        private final List<ViewResolver> viewResolvers;
        private final ServerCodecConfigurer serverCodecConfigurer;

        @Autowired
        public WebFluxSentinelConfig(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                     ServerCodecConfigurer serverCodecConfigurer) {
            this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
            this.serverCodecConfigurer = serverCodecConfigurer;
        }

        public void initRule() {
            List<FlowRule> rules = new ArrayList<>();
            FlowRule rule = new FlowRule();
            rule.setResource("/admin/*");
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            // Set limit QPS.
            rule.setCount(100);
            rules.add(rule);
            FlowRuleManager.loadRules(rules);
        }

        @Bean
        @Order(-1)
        public SentinelBlockExceptionHandler sentinelBlockExceptionHandler() {
            // Register the block exception handler for Spring WebFlux.
            return new SentinelBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
        }

        @Bean
        @Order(-1)
        public SentinelWebFluxFilter sentinelWebFluxFilter() {
            // Register the Sentinel WebFlux filter.
            return new SentinelWebFluxFilter();
        }

    }

    /**
     * Fetch metric of local.
     */
    @Slf4j
    private static class LocalDashboardMetricFetcherService {

        private LocalMetricSearcher localMetricSearcher = new LocalMetricSearcher();

        private long fetchIntervalMillis;

        private static final String NO_METRICS = "No metrics";
        private String appName = "sentinel-com.github.rd.sentinel.dashboard";

        private Map<String, AtomicLong> appLastFetchTime = new ConcurrentHashMap<>();
        private AtomicLong lastFetchTime = new AtomicLong(System.currentTimeMillis());

        @SuppressWarnings("PMD.ThreadPoolCreationRule")
        private ExecutorService fetchWorker;

        public LocalDashboardMetricFetcherService(DashboardResourceManagerProperties dashboardResourceManagerProperties) {
            this.fetchIntervalMillis = dashboardResourceManagerProperties.getFetchMetricIntervalSeconds() * 1000;
            int corePoolSize = 1;
            int maxPoolSize = 5;
            long keepAliveTime = 0;
            int queueSize = 50;
            RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
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

            System.out.println("时间间隔" + (endTime - startTime));
            System.out.println("$与当前时间的差距" + (now - endTime));
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
                        log.info("fetchOnce(" + appName + ") error", e);
                    }
                });
            } catch (Exception e) {
                log.info("submit fetchOnce(" + appName + ") fail, intervalMs [" + startTime + ", " + endTime + "]", e);
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
            System.out.println("todo 拉取本地指标，待持久化" + JSONObject.toJSON(map));
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
                    log.warn("handle metric exception, metric: {}", line);
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

    @Slf4j
    public static class LocalMetricSearcher {

        private volatile MetricSearcher searcher;

        private final int maxLines = 6000;

        private final Object lock = new Object();

        public String search(Long startTime, Long endTime, String identity) {
            // Note: not thread-safe.
            if (searcher == null) {
                synchronized (lock) {
                    String appName = SentinelConfig.getAppName();
                    if (appName == null) {
                        appName = "";
                    }
                    if (searcher == null) {
                        searcher = new MetricSearcher(MetricWriter.METRIC_BASE_DIR,
                                MetricWriter.formMetricFileName(appName, PidUtil.getPid()));
                    }
                }
            }
            List<MetricNode> list;
            try {
                // Find by end time if set.
                if (endTime != null) {
                    list = searcher.findByTimeAndResource(startTime, endTime, identity);
                } else {
                    list = searcher.find(startTime, maxLines);
                }
            } catch (Exception ex) {
                log.error("Error when retrieving metrics", ex);
                return null;
            }
            if (list == null) {
                list = new ArrayList<>();
            }
            if (StringUtil.isBlank(identity)) {
                addCpuUsageAndLoad(list);
            }
            StringBuilder sb = new StringBuilder();
            for (MetricNode node : list) {
                sb.append(node.toThinString()).append("\n");
            }
            return sb.toString();
        }

        /**
         * add current cpu usage and load to the metric list.
         *
         * @param list metric list, should not be null
         */
        private void addCpuUsageAndLoad(List<MetricNode> list) {
            long time = TimeUtil.currentTimeMillis() / 1000 * 1000;
            double load = SystemRuleManager.getCurrentSystemAvgLoad();
            double usage = SystemRuleManager.getCurrentCpuUsage();
            if (load > 0) {
                MetricNode loadNode = toNode(load, time, Constants.SYSTEM_LOAD_RESOURCE_NAME);
                list.add(loadNode);
            }
            if (usage > 0) {
                MetricNode usageNode = toNode(usage, time, Constants.CPU_USAGE_RESOURCE_NAME);
                list.add(usageNode);
            }
        }

        /**
         * transfer the value to a MetricNode, the value will multiply 10000 then truncate
         * to long value, and as the {@link MetricNode#passQps}.
         * <p>
         * This is an eclectic scheme before we have a standard metric format.
         * </p>
         *
         * @param value    value to save.
         * @param ts       timestamp
         * @param resource resource name.
         * @return a MetricNode represents the value.
         */
        private MetricNode toNode(double value, long ts, String resource) {
            MetricNode node = new MetricNode();
            node.setPassQps((long) (value * 10000));
            node.setTimestamp(ts);
            node.setResource(resource);
            return node;
        }
    }

    @Component
    @ConfigurationProperties("rd-sentinel-dashboard.dashboard-service-manager.fetch-metric-interval-seconds")
    @Slf4j
    public static class DashboardResourceManagerProperties {
        /**
         * 拉取指标的间隔
         */
        private int fetchMetricIntervalSeconds = 6;

        public int getFetchMetricIntervalSeconds() {
            if (fetchMetricIntervalSeconds < 3 || fetchMetricIntervalSeconds > 8) {
                throw new IllegalArgumentException("3 <= rd-sentinel-dashboard.dashboard-service-manager.fetch-metric-interval-seconds >= 7");
            }
            return fetchMetricIntervalSeconds;
        }

        public void setFetchMetricIntervalSeconds(int fetchMetricIntervalSeconds) {
            this.fetchMetricIntervalSeconds = fetchMetricIntervalSeconds;
        }

    }
}
