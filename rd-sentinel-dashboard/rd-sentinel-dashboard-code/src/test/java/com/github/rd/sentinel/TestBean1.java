package com.github.rd.sentinel;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class TestBean1 implements InitializingBean, DisposableBean, ApplicationRunner, CommandLineRunner {

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(InitializingBean.class.getName()+".afterPropertiesSet()");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println(PostConstruct.class.getName());
    }

    @Override
    public void destroy() throws Exception {
        //Spring Framework 官方并不建议我们通过这种方法来销毁 bean，这同样是一种强耦合的方式，我们看到框架层面才会用到这个方法
        System.out.println(DisposableBean.class.getName()+".destroy()");
    }

    @PreDestroy
    public void preDestroy() {
        //这种方式是 Spring 非常提倡的一种方式
        System.out.println(PreDestroy.class.getName());
    }

    public void initMethod() {
        System.out.println("@Bean(initMethod = \"initMethod\")");
    }

    public void destroyMethod() {
        System.out.println("@Bean(destroyMethod = \"destroyMethod\")");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(ApplicationRunner.class.getName()+".run()");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(CommandLineRunner.class.getName()+".run()");
    }

}
