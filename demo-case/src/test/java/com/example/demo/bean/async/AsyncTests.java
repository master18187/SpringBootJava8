package com.example.demo.bean.async;

import com.example.demo.spring.async.AsyncServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsyncTests {
        private AsyncServiceImpl asyncService;

        @BeforeEach
        void setUp() {
            // Mockito.mock(AsyncServiceImpl.class);
            asyncService = new AsyncServiceImpl();
        }

        @Test
        @DisplayName("异步线程验证")
        void asyncTask() throws ExecutionException, InterruptedException {
            // 准备Mock数据

            // 执行测试
            asyncService.voidAsyncTask();
            Future<String> stringFuture = asyncService.futureAsyncTask();
            CompletableFuture<String> stringCompletableFuture = asyncService.asyncCompletableMethod();

            // 验证结果
            assertEquals("success", stringFuture.get());
            assertEquals("执行完成", stringCompletableFuture.get());
        }
}
