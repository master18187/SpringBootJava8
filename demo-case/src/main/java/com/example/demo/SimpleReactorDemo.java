package com.example.demo;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

public class SimpleReactorDemo {

    public static void main(String[] args) {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.setThreadFactory(new CustomizableThreadFactory("demo-task-"));
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();

        Mono<Void> just = Mono.fromCallable(() -> {
            return null;
        }).subscribeOn(Schedulers.fromExecutor(taskExecutor)).then();

        Mono<Object> objectMono = Mono.fromRunnable(() -> {

        }).subscribeOn(Schedulers.fromExecutor(taskExecutor));
        FutureTask<Void> task1 = new FutureTask<>(() -> {
            return null;
        });
        // Mono.zip(task1).block()
    }
}
