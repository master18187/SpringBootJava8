package com.example.demo.spring.dynamicbean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;

@Slf4j
public class SimpleDynamicServiceImpl implements ApplicationContextAware, BeanFactoryAware, BeanNameAware, DynamicService, InitializingBean {

    private BeanFactory beanFactory;

    private String beanName;

    @PostConstruct
    public void init() {
        log.info("SimpleDynamicServiceImpl.init " + beanName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("SimpleDynamicServiceImpl.afterPropertiesSet " + beanName);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.info("SimpleDynamicServiceImpl.setBeanFactory " + beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("SimpleDynamicServiceImpl.setApplicationContext " + beanName);
    }

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }
}
