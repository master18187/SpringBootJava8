package com.example;

import com.example.demo.bean.demo1.Service1;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(proxyTargetClass = false) // true 使用CGLIB代理

@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = SpringApplication.run(ExampleApplication.class, args);

        Service1 service1 = applicationContext.getBean("service1", Service1.class);
        service1.echo();
    }

}