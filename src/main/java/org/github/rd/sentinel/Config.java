package org.github.rd.sentinel;

import com.sun.jdi.connect.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Config {
    @Bean
    public RouterFunction<ServerResponse> initRouterFunction(){
        return RouterFunctions.route()
                .GET("/hello/{name}",serverRequest -> {
                    String name=serverRequest.pathVariable("name");

                    return ServerResponse.ok().bodyValue(name);
                })
                .POST("/hello2",serverRequest -> {
                    String name=serverRequest.exchange().getRequest().getQueryParams().getFirst("name");

                    assert name != null;
                    return ServerResponse.ok().bodyValue(name);
                })
                .build();
    }

}
