package com.example.demo.spring.dynamicmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

public class DynamicConfigLoader {
    public static List<DynamicEndpointConfig> loadConfig(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(resource.getInputStream(),
                mapper.getTypeFactory().constructCollectionType(List.class, DynamicEndpointConfig.class));
    }
}
