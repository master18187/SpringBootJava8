package com.example.demo.spring.dynamicmvc;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

// @RestController
public class DynamicController {
    @RequestMapping(path = "/**")
    public Object handleRequest(HttpServletRequest request) {
        // 根据请求路径动态返回数据
        String path = request.getRequestURI();
        // 实际应用中应从缓存或数据库读取配置
        return "Dynamic response for: " + path;
    }
}
