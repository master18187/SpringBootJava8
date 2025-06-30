package com.example.demo.jdk.jdklock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TestMain {

    // static ResourceHolder resourceHolder = new ResourceHolder();
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool(new CustomizableThreadFactory("test-"));
        ReentrantLock lock = new ReentrantLock();
        executorService.submit(() -> {
            try {

                boolean b = lock.tryLock(10, TimeUnit.SECONDS);
                log.info("Thead " + Thread.currentThread().getName() + " get lock " + b);
                ResourceHolder.res--;
                Thread.sleep(12000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        executorService.submit(() -> {
            try {

                boolean b = lock.tryLock(10, TimeUnit.SECONDS);
                log.info("Thead " + Thread.currentThread().getName() + " get lock " + b);
                ResourceHolder.res--;
                Thread.sleep(12000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // 信号量，最大并发数等控制
        Semaphore semaphore = new Semaphore(3, false);

        executorService.submit(() -> semaphore.tryAcquire(1));
        executorService.submit(() -> semaphore.tryAcquire(1));
        executorService.submit(() -> semaphore.tryAcquire(1));

        // 栅栏，达到数量一次性释放
        CyclicBarrier cb = new CyclicBarrier(3, () -> {
            log.info("after CyclicBarrier ok");
        });

        for (int i = 0; i < 3; i++) {
            executorService.submit(() -> {
                System.out.println(Thread.currentThread().getName() + " 开始准备...");
                try {
                    Thread.sleep((long) (Math.random() * 1000)); // 随机准备时间
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + " 准备就绪，等待其他线程...");
                try {
                    cb.await(); // 等待其他线程
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + " 继续执行后续任务！");
            });
        }


        Thread.sleep(100);
        log.info("res = " + ResourceHolder.res);
    }
}
