package io.github.rd.sentinel;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class MyInitializers implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        System.out.println(ApplicationContextInitializer.class.getName()+".initialize() 通常在web应用中，设计在初始化Spring容器之前调用。例如依赖于容器ConfigurableApplicationContext中的Enviroment来记录一些配置信息或者使一些配置文件生效；");
    }
}
