package com.example.demo.bean.async;

import com.example.demo.spring.async.AsyncServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AsyncSpringTests {

    @Autowired
    private AsyncServiceImpl asyncService;

    @Test
    void asyncTask() throws ExecutionException, InterruptedException {
        // 执行测试
        asyncService.voidAsyncTask();
        Future<String> stringFuture = asyncService.futureAsyncTask();
        CompletableFuture<String> stringCompletableFuture = asyncService.asyncCompletableMethod();

        // 验证结果
        assertEquals("success", stringFuture.get());
        assertEquals("执行完成", stringCompletableFuture.get());
    }
}
