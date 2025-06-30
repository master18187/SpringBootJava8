package com.example.demo.spring.dynamicmvc;

import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

public class DynamicHandlerMapping extends AbstractHandlerMapping {
    private final Map<String, Method> handlerMethods;

    public DynamicHandlerMapping(Map<String, Method> handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return handlerMethods.get(uri);
    }
}
