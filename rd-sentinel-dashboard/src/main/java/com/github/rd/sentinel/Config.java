package com.github.rd.sentinel;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.http.server.reactive.HttpHandler;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.LoopResources;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;


//https://cloud.tencent.com/developer/ask/180811
//https://blog.csdn.net/weixin_34383618/article/details/89590931
//netty自定义工作线程 https://blog.csdn.net/weixin_43364172/article/details/89210706
//webflux官方文档 https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux
//reactor netty官方文档 https://projectreactor.io/docs/netty/release/reference/index.html 配置方法https://projectreactor.io/docs/netty/release/reference/index.html#_tcp_level_configuration
//netty参数配置详解https://blog.csdn.net/qq_33797928/article/details/107205995?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-0&spm=1001.2101.3001.4242 https://www.jianshu.com/p/975b30171352 https://blog.csdn.net/WillPan1234/article/details/58598220
//netty资源隔离配置https://blog.csdn.net/weixin_43364172/article/details/88720097
//NettyWebServerFactoryCustomizer

//设置线程的代码块 org.springframework.boot.autoconfigure.web.embedded.TomcatWebServerFactoryCustomizer.customizeMaxThreads
//容器初始化的入口 EmbeddedWebServerFactoryCustomizerAutoConfiguration
//springboot 直接集成netty https://www.jianshu.com/p/b60180a0a0e6
//zookeeper三个端口https://www.cnblogs.com/yougewe/p/11728073.html
//AdminServerFactory
//netty的资源配置ReactorResourceFactory，线程配置LoopResources
@Configuration
public class Config {

    /*@Autowired
    HttpHandler httpHandler;

    WebServer http1;

    @PostConstruct
    public void start() {

        NettyReactiveWebServerFactory nettyFactory = new NettyReactiveWebServerFactory(8000);
        nettyFactory.setResourceFactory(null);

        nettyFactory.addServerCustomizers(server ->
                server.runOn(LoopResources.create("server", 1, 8, true))
        );
        ReactorResourceFactory reactorResourceFactory = new ReactorResourceFactory();
        reactorResourceFactory.setLoopResources(LoopResources.create("server", 1, 8, true));

        nettyFactory.setResourceFactory(reactorResourceFactory);
        this.http1 = nettyFactory.getWebServer(this.httpHandler);
        this.http1.start();

    }

    @PreDestroy
    public void stop() {
        this.http1.stop();
    }*/

    /*@Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory(1111);
        int[] ports = {8000, 8100};
        List<Connector> connectors = new ArrayList<>();
        for (int port:ports){
            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            connector.setPort(port);
            connector.setScheme("http");
            connectors.add(connector);
        }
        tomcat.addAdditionalTomcatConnectors(connectors.toArray(connectors.toArray(new Connector[] {})));
        tomcat.setPort(9999);
        return tomcat;
    }*/

}
