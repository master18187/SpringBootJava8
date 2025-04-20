package com.example.demo.spring.mvcmapplng;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> helloRouter() {
        return RouterFunctions.route(
                RequestPredicates.GET("/func/hello"),
                request -> ServerResponse.ok().body("Hello from RouterFunction!")
        );
    }
}
