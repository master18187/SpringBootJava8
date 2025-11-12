package com.example;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义Session监听器
 * 用于监听Session的创建、停止和过期事件
 */
@Component
public class CustomSessionListener implements SessionListener {

    private static final Logger logger = LoggerFactory.getLogger(CustomSessionListener.class);
    
    /**
     * 活跃Session计数器
     */
    private final AtomicInteger activeSessionCount = new AtomicInteger(0);
    
    /**
     * Session创建事件
     */
    @Override
    public void onStart(Session session) {
        activeSessionCount.incrementAndGet();
        logger.info("Session started: {}, total active sessions: {}", 
                   session.getId(), activeSessionCount.get());
    }
    
    /**
     * Session停止事件
     */
    @Override
    public void onStop(Session session) {
        activeSessionCount.decrementAndGet();
        logger.info("Session stopped: {}, total active sessions: {}", 
                   session.getId(), activeSessionCount.get());
    }
    
    /**
     * Session过期事件
     */
    @Override
    public void onExpiration(Session session) {
        activeSessionCount.decrementAndGet();
        logger.info("Session expired: {}, total active sessions: {}", 
                   session.getId(), activeSessionCount.get());
    }
    
    /**
     * 获取当前活跃Session数量
     */
    public int getActiveSessionCount() {
        return activeSessionCount.get();
    }
    
    /**
     * 重置计数器
     */
    public void reset() {
        activeSessionCount.set(0);
        logger.info("Session listener reset");
    }
}