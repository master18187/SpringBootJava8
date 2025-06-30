package com.example.demo.spring.dynamicmvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Configuration
public class DynamicWebConfig /*extends WebMvcConfigurationSupport*/  implements WebMvcConfigurer {
    // 创建自定义的 RequestMappingHandlerMapping
    /*@Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new DynamicHandlerMapping(null);
    }
    // 自定义 HandlerAdapter
    @Override
    protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
        return new RequestMappingHandlerAdapter();
    }*/


   /* @Bean
    public SimpleUrlHandlerMapping dynamicHandlerMapping() {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setUrlMap(new ConcurrentHashMap<String, Controller>()); // 动态路由表
        handlerMapping.setOrder(-1);
        return handlerMapping;
    }*/

    // 提供动态注册路由的公共方法
   /* public void addDynamicRoute(String path, Controller controller) {
        SimpleUrlHandlerMapping handlerMapping =
                (SimpleUrlHandlerMapping) dynamicHandlerMapping();
        Map<String, Controller> urlMap = handlerMapping.getUrlMap();
        urlMap.put(path, controller);
        // 刷新路由（需要调用注册逻辑）
    }
*/
}
