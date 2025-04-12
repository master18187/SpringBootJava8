package com.example.demo;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public class SimpleCompletableFutureDemo {
    private static final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

    static {
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.setThreadFactory(new CustomizableThreadFactory("demo-task-"));
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
    }

    public static void main(String[] args) {


        // 有返回值
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "Task1", taskExecutor);
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "Task2", taskExecutor);
        CompletableFuture<String> cf3 = CompletableFuture.supplyAsync(() -> "Task3", taskExecutor);

        CompletableFuture<Void> all = CompletableFuture.allOf(cf1, cf2, cf3);
        all.thenRun(() -> {
            System.out.println("All tasks completed");
            System.out.println("Results: " + cf1.join() + ", " + cf2.join() + ", " + cf3.join());
        });

        // 无返回值
        CompletableFuture<Void> cf11 = CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + " Task1"), taskExecutor);
        CompletableFuture<Void> cf22 = CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + " Task2"), taskExecutor);
        CompletableFuture<Void> cf33 = CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + " Task3"), taskExecutor).whenComplete((rex, ex) -> {

        });
        CompletableFuture<Void> all1 = CompletableFuture.allOf(cf11, cf22, cf33);

        all.thenRun(() -> {
            System.out.println(Thread.currentThread().getName() + " All tasks completed");
        });

        taskExecutor.shutdown();
    }
}
