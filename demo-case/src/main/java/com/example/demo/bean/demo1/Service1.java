package com.example.demo.bean.demo1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class Service1 {

    @Autowired
    Service2 service2;

    @Log
    public void echo() {
        System.out.println("service1");
        service2.echo();
    }
}
