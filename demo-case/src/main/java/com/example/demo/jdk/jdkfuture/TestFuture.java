package com.example.demo.jdk.jdkfuture;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestFuture {


    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool(new CustomizableThreadFactory("test-"));

        // submit 内部封装了FutureTask，比AQS更轻量的线程等待
        // NEW -> COMPLETING -> NORMAL
        // NEW -> COMPLETING -> EXCEPTIONAL
        // NEW -> CANCELLED
        // NEW -> INTERRUPTING -> INTERRUPTED
        // FutureTask 内部状态
        // 0 NEW 新建
        // 1 COMPLETING 进行中
        // 2 NORMAL 正常完成
        // 3 EXCEPTIONAL 异常
        // 4 CANCELLED 已取消
        // 5 INTERRUPTING 中断中
        // 6 INTERRUPTED 已中断
        Future<?> submit = executorService.submit(() -> System.out.println("111"));
        // 线程状态
        // NEW
        // RUNNABLE
        // BLOCKED
        // WAITING TIMED_WAITING
        // TERMINATED

        // AQS 内部NODE节点状态
        // -3 PROPAGATE 传播
        // -2 CONDITION 条件等待
        // -1 SIGNAL 信号
        // 0 初始化
        // 1 CANCELLED 已取消

    }

}
