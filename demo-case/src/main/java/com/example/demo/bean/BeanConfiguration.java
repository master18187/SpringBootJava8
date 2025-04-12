package com.example.demo.bean;


import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class BeanConfiguration {

    @Bean
    public DemoBeanPostProcessor demoBeanPostProcessor() {
        return new DemoBeanPostProcessor();
    }
}
