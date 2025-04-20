package com.example.demo.spring.dynamicmvc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicControllerGenerator {
    private final Map<String, Method> handlerMethods = new HashMap<>();

    public void generateControllerMethods(List<DynamicEndpointConfig> configs) throws Exception {
        for (DynamicEndpointConfig config : configs) {
            Method method = DynamicController.class.getMethod("handleRequest", Object.class);
            handlerMethods.put(config.getPath(), method);
        }
    }

    public Map<String, Method> getHandlerMethods() {
        return handlerMethods;
    }
}
