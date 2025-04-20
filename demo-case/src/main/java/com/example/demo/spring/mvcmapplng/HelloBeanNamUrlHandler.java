package com.example.demo.spring.mvcmapplng;

import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("/hello") // Bean 名称即 URL 路径
public class HelloBeanNamUrlHandler implements HttpRequestHandler {
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.getWriter().write("Hello from BeanNameUrlHandlerMapping!");
    }
}
