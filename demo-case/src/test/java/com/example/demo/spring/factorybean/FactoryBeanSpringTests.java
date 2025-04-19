package com.example.demo.spring.factorybean;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FactoryBeanSpringTests {

    @Autowired
    private ProviderServiceImpl factoryBean;

    @Test
    void asyncTask() {
        // 执行测试
        factoryBean.simpleExec();
    }
}
