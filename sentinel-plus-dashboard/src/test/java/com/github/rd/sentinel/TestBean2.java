package io.github.rd.sentinel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;

import javax.annotation.PreDestroy;

public class TestBean2 implements ApplicationRunner {
    @Autowired
    private ApplicationContext applicationContext;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        defaultListableBeanFactory.removeBeanDefinition("TestBean2");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("销毁"+TestBean2.class.getName());
    }
}
