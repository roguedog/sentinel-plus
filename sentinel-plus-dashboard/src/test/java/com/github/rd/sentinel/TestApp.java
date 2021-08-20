package io.github.rd.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class TestApp {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TestApp.class);
        application.addInitializers(new MyInitializers());
        application.run(args);
    }

    @Bean(initMethod = "initMethod", destroyMethod = "destroyMethod")
    public TestBean1 test() {
        return new TestBean1();
    }
}
