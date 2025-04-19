package com.example.demo.spring.factorybean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FactoryBeanConfiguration {

    @Bean
    @Primary
    public SimpleFactoryBean simpleFactoryBean() {
        return new SimpleFactoryBean();
    }

    @Bean
    public DynamicBean dynamicBean() {
        return new DynamicBean() {};
    }
}
