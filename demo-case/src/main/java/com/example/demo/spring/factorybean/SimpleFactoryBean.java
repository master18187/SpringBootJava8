package com.example.demo.spring.factorybean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

import java.util.HashMap;

@Slf4j
public class SimpleFactoryBean<T> implements FactoryBean<T> {

    @Override
    public T getObject() throws Exception {
        return (T) new DynamicBean() {};
    }

    @Override
    public Class<?> getObjectType() {
        return DynamicBean.class;
    }


}
