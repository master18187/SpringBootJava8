package com.example.demo.spring.dynamicbean;

import com.example.demo.spring.factorybean.ProviderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DynamicServiceSpringTests {

    @Autowired
    private DynamicServiceProvider dynamicServiceProvider;

    @Test
    void dynamicServiceProvider() {
        // 执行测试
    }
}
