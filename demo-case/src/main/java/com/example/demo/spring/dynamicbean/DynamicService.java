package com.example.demo.spring.dynamicbean;

public interface DynamicService {



    default void hello() {
        System.out.println("hello");
    }
}
