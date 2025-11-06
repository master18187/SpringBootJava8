package com.example;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HelloControllerTest {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @BeforeEach
    void setUp() {
        // 清理测试数据

    }

    @Test
    @Order(1)
    void shouldCreateUser() {
        String resp = restTemplate.postForObject(
                "http://localhost:" + port + "/", null, String.class);
        System.out.println(resp);
        resp = restTemplate.postForObject(
                "http://localhost:" + port + "/user", null, String.class);
        System.out.println(resp);
    }

    @Test
    @Order(2)
    void shouldGetUserById() {

    }
}