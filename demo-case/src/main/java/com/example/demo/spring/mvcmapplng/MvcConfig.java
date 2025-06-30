package com.example.demo.spring.mvcmapplng;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Collections;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 以下是 Spring MVC 中 BeanNameUrlHandlerMapping、RouterFunctionMapping、HttpRequestHandlerAdapter 和 SimpleControllerHandlerAdapter 的详细使用说明及配置方法：
     * <p>
     * 一、BeanNameUrlHandlerMapping 的使用
     * 1. 作用
     * 将 Bean 的名称作为 URL 路径映射到对应的处理器。例如，若存在名为 /hello 的 Bean，则请求 /hello 会路由到该 Bean。
     *
     * @return
     */
    @Bean
    public BeanNameUrlHandlerMapping beanNameUrlHandlerMapping() {
        BeanNameUrlHandlerMapping handlerMapping = new BeanNameUrlHandlerMapping();
        handlerMapping.setOrder(2); // 设置优先级（默认最低）
        return handlerMapping;
    }

    /**
     * RouterFunctionMapping 的使用
     * 1. 作用
     * 用于处理基于函数式编程模型的路由（需 Spring WebFlux 或 Spring MVC 5.2+）。
     *
     * 2. 添加依赖（Spring MVC 中使用 WebFlux）
     * @return
     */
    @Bean("customRouterFunctionMapping")
    @Primary
    public RouterFunctionMapping routerFunctionMapping() {
        RouterFunctionMapping mapping = new RouterFunctionMapping();
        mapping.setOrder(0); // 设置最高优先级
        return mapping;
    }


    /**
     * 三、HttpRequestHandlerAdapter 的配置
     * 1. 作用
     * 处理实现了 HttpRequestHandler 接口的处理器（常用于静态资源处理）。
     *
     * 2. 默认配置
     * Spring Boot 自动配置 HttpRequestHandlerAdapter，无需手动注册。
     * @return
     */
    // 自定义静态资源处理器
    @Bean
    public HttpRequestHandler customResourceHandler() {
        return (request, response) -> {
            response.getWriter().write("Custom Resource Response");
        };
    }
    // 映射路径到该处理器

    @Bean
    public SimpleUrlHandlerMapping customResourceHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(Collections.singletonMap("/resources/**", customResourceHandler()));
        mapping.setOrder(2);
        return mapping;
    }

}
