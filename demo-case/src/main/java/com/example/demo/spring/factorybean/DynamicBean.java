package com.example.demo.spring.factorybean;

public interface DynamicBean {

    default void echo() {
        System.out.println("hello");
    }
}
