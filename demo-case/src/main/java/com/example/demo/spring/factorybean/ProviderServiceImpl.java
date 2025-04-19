package com.example.demo.spring.factorybean;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProviderServiceImpl {

    @Autowired
    @Qualifier(BeanFactory.FACTORY_BEAN_PREFIX + "simpleFactoryBean")
    SimpleFactoryBean simpleFactoryBean;

    // 要指定名称，或加@Primary注解
    @Autowired
    DynamicBean bean;

    public void simpleExec() {
        System.out.println(simpleFactoryBean);
        System.out.println(bean);
    }
}
