package com.github.rd.sentinel.dashboard.datasource;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;

@SpringBootApplication
public class DataSourceApp {
    public static void main(String[] args) {
        SpringApplication.run(DataSourceApp.class, args);
//        runJettyServer();
    }

    static void runJettyServer() {
        new NettyReactiveWebServerFactory();
        JettyServletWebServerFactory jettyServletWebServerFactory = new JettyServletWebServerFactory(8000);
        jettyServletWebServerFactory.getWebServer().start();
        JettyServletWebServerFactory jettyServletWebServerFactory1 = new JettyServletWebServerFactory(8100);
        jettyServletWebServerFactory1.getWebServer().start();
    }

    /*static void runNettyServer() {
    //netty使用文档 https://projectreactor.io/docs/netty/release/reference/index.html#_starting_and_stopping_2
        //reactor-netty-server的reactor.netty.http.server.HttpServerBind是单例模式，只能创建一个Http服务
        DisposableServer server = new MyNettyHttpServer()
//                .option(ChannelOption) //自定义配置TCP
//                .channelGroup() //广播消息
                .accessLog(true)
                .port(8888)
                .host("localhost")
                .route(routes -> routes.get("/{id}", (request, response) -> {
                    System.out.println(request.param("id"));
                    return response.sendString(Mono.just(request.param("id")));
                }))
                .bindNow();
        server.onDispose().block();
    }*/
}
