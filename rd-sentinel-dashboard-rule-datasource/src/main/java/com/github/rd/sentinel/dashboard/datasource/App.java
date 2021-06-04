package com.github.rd.sentinel.dashboard.datasource;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
//netty使用文档 https://projectreactor.io/docs/netty/release/reference/index.html#_starting_and_stopping_2
//
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        //reactor-netty-server的reactor.netty.http.server.HttpServerBind是单例模式，只能创建一个Http服务
        DisposableServer server = HttpServer.create()
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

    }
}
