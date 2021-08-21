package io.github.roguedog.sentinel.plus.example.application;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LeaderSelectorTest implements ApplicationRunner {
    private static final String PATH = "/demo/leader";

    public static void main(String[] args) {
        start();
    }

    public static void start() {

        List<LeaderSelector> selectors = new ArrayList<>();
        List<CuratorFramework> clients = new ArrayList<>();
        try {
            for (int i = 0; i < 3; i++) {
                CuratorFramework client = getClient();
                clients.add(client);

                final String name = "client#" + i;
                LeaderSelector leaderSelector = new LeaderSelector(client, PATH, new LeaderSelectorListener() {
                    @Override
                    public void takeLeadership(CuratorFramework client) throws Exception {

                        System.out.println(name + ":I am leader.");
                        Thread.sleep(2000);
                    }

                    @Override
                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
                        System.out.println("change");
                    }
                });

                leaderSelector.autoRequeue();
                leaderSelector.start();
                selectors.add(leaderSelector);

            }
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for(CuratorFramework client : clients){
                CloseableUtils.closeQuietly(client);
            }

            for(LeaderSelector selector : selectors){
                CloseableUtils.closeQuietly(selector);
            }

        }
    }

    private static CuratorFramework getClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(6000)
                .connectionTimeoutMs(3000)
                .namespace("demo")
                .build();
        client.start();
        return client;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(() -> start()).start();
    }
}
