package com.example.demo.spring.dynamicmvc;

import lombok.Data;

@Data
public class DynamicEndpointConfig {
    private String path;
    private String method;
    private Object response;

}
