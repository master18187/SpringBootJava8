package com.example.demo.spring.async;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer, EnvironmentAware {

    private Environment environment;
    /**
     * 默认异步线程池配置
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        /**
         * CPU密集型任务	IO密集型任务	混合型任务
         * corePoolSize	CPU核数 + 1	CPU核数 × 2	CPU核数 × 1.5
         * maxPoolSize	corePoolSize	corePoolSize × 2	corePoolSize × 1.5
         * queueCapacity	0-50	100-500	50-200
         * keepAliveTime	60s	120s	90s
         */
        String[] activeProfiles = environment.getActiveProfiles();
        String corePoolSize = environment.getProperty("spring.async.core-pool-size", String.valueOf(Runtime.getRuntime().availableProcessors()));
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Integer.parseInt(corePoolSize));
        executor.setMaxPoolSize(executor.getPoolSize() * 2);
        executor.setAllowCoreThreadTimeOut(false);
        // 小于等于0 SynchronousQueue同步等待队列
        // 大于0 LinkedBlockingQueue
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Default-Async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 非核心线程保持时长
        executor.setKeepAliveSeconds(60);
        executor.setDaemon(false);
        // executor.setTaskDecorator(new ContextCopyingDecorator());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            System.err.println("异步方法异常: " + method.getName());
            ex.printStackTrace();
            // 可扩展：发送告警/记录错误数据库等
        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    static class ContextCopyingDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            // 复制上下文信息
            RequestAttributes context = RequestContextHolder.currentRequestAttributes();
            return () -> {
                try {
                    RequestContextHolder.setRequestAttributes(context);
                    runnable.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        }
    }
}
