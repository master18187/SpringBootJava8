package com.example.demo.spring.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@Service
public class AsyncServiceImpl {

    @Async
    public void voidAsyncTask() {
        log.info("voidAsyncTask...");
        log.info("直接调用无法阻塞线程获取结果...");
    }

    /**
     * 有返回值异步，可等待异常捕获
     * @return
     */
    @Async
    public Future<String> futureAsyncTask() {
        log.info("futureAsyncTask...");
        log.info("可线程阻塞获取结果，可捕获异常...");
        return new AsyncResult<>("success");
    }

    /**
     * 有返回值异步，返回CompletableFuture可等待异常捕获
     * @return
     */
    @Async
    public CompletableFuture<String> asyncCompletableMethod() {
        // 返回CompletableFuture（Spring 4.2+）
        log.info("asyncCompletableMethod...");
        log.info("可线程阻塞获取结果，可捕获异常...");
        return CompletableFuture.supplyAsync(() -> "执行完成");
    }
}
