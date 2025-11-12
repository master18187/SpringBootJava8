package com.example;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 增强版内存SessionDAO
 * 提供Session统计和自动清理功能
 */
@Component("customMemorySessionDAO")
public class CustomMemorySessionDAO extends MemorySessionDAO {

    private static final Logger logger = LoggerFactory.getLogger(CustomMemorySessionDAO.class);
    
    /**
     * Session最大活跃数量
     */
    @Value("${shiro.session.memory.maxActive:1000}")
    private int maxActiveSessions;
    
    /**
     * 自动清理间隔（分钟）
     */
    @Value("${shiro.session.memory.cleanupInterval:30}")
    private int cleanupInterval;
    
    /**
     * 启用自动清理
     */
    @Value("${shiro.session.memory.autoCleanup:true}")
    private boolean autoCleanup;
    
    /**
     * Session创建时间记录
     */
    private final ConcurrentHashMap<Serializable, Long> sessionCreationTimes = new ConcurrentHashMap<>();
    
    /**
     * 定时清理任务
     */
    private ScheduledExecutorService cleanupExecutor;
    
    /**
     * 构造函数
     */
    public CustomMemorySessionDAO() {
        super();
        if (autoCleanup) {
            startCleanupTask();
        }
    }
    
    /**
     * 创建Session
     */
    @Override
    protected Serializable doCreate(Session session) {
        // 检查Session数量限制
        if (getActiveSessions().size() >= maxActiveSessions) {
            logger.warn("Session limit exceeded ({}), cleaning up expired sessions", maxActiveSessions);
            cleanupExpiredSessions();
            
            // 如果还是超过限制，移除最老的Session
            if (getActiveSessions().size() >= maxActiveSessions) {
                removeOldestSession();
            }
        }
        
        Serializable sessionId = super.doCreate(session);
        sessionCreationTimes.put(sessionId, System.currentTimeMillis());
        
        logger.debug("Session {} created, total sessions: {}", sessionId, getActiveSessions().size());
        return sessionId;
    }
    
    /**
     * 更新Session
     */
    @Override
    public void update(Session session) {
        super.update(session);
        logger.debug("Session {} updated", session.getId());
    }

    /**
     * 删除Session
     */
    @Override
    public void delete(Session session) {
        Serializable sessionId = session.getId();
        super.delete(session);
        sessionCreationTimes.remove(sessionId);
        
        logger.debug("Session {} deleted, total sessions: {}", sessionId, getActiveSessions().size());
    }
    
    /**
     * 获取活跃Session数量
     */
    public int getActiveSessionCount() {
        return getActiveSessions().size();
    }
    
    /**
     * 获取最大活跃Session数
     */
    public int getMaxActiveSessions() {
        return maxActiveSessions;
    }
    
    /**
     * 设置最大活跃Session数
     */
    public void setMaxActiveSessions(int maxActiveSessions) {
        this.maxActiveSessions = maxActiveSessions;
    }
    
    /**
     * 获取Session的创建时间
     */
    public long getSessionCreationTime(Serializable sessionId) {
        return sessionCreationTimes.getOrDefault(sessionId, 0L);
    }
    
    /**
     * 清理过期的Session
     */
    public void cleanupExpiredSessions() {
        int cleanedCount = 0;
        Collection<Session> sessions = getActiveSessions();
        
        for (Session session : sessions) {
            if (session != null) {
                try {
                    // session.validate();
                } catch (Exception e) {
                    // Session已过期，删除它
                    delete(session);
                    cleanedCount++;
                }
            }
        }
        
        if (cleanedCount > 0) {
            logger.info("Cleaned up {} expired sessions", cleanedCount);
        }
    }
    
    /**
     * 移除最老的Session
     */
    private void removeOldestSession() {
        Serializable oldestSessionId = null;
        long oldestTime = Long.MAX_VALUE;
        
        for (Map.Entry<Serializable, Long> entry : sessionCreationTimes.entrySet()) {
            if (entry.getValue() < oldestTime) {
                oldestTime = entry.getValue();
                oldestSessionId = entry.getKey();
            }
        }
        
        if (oldestSessionId != null) {
            Session oldestSession = readSession(oldestSessionId);
            if (oldestSession != null) {
                delete(oldestSession);
                logger.info("Removed oldest session {} due to limit exceeded", oldestSessionId);
            }
        }
    }
    
    /**
     * 启动自动清理任务
     */
    private void startCleanupTask() {
        cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SessionDAOCleanup");
            t.setDaemon(true);
            return t;
        });
        
        cleanupExecutor.scheduleAtFixedRate(
            this::cleanupExpiredSessions,
            cleanupInterval,
            cleanupInterval,
            TimeUnit.MINUTES
        );
        
        logger.info("Session cleanup task started with interval: {} minutes", cleanupInterval);
    }
    
    /**
     * 停止自动清理任务
     */
    public void stopCleanupTask() {
        if (cleanupExecutor != null && !cleanupExecutor.isShutdown()) {
            cleanupExecutor.shutdown();
            try {
                if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    cleanupExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                cleanupExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            logger.info("Session cleanup task stopped");
        }
    }
    
    /**
     * 获取Session统计信息
     */
    public SessionStats getSessionStats() {
        Collection<Session> sessions = getActiveSessions();
        int validSessions = 0;
        int expiredSessions = 0;
        long now = System.currentTimeMillis();
        
        for (Session session : sessions) {
            if (session != null) {
                try {
                    // session.validate();
                    validSessions++;
                } catch (Exception e) {
                    expiredSessions++;
                }
            }
        }
        
        return new SessionStats(
            sessions.size(),
            validSessions,
            expiredSessions,
            maxActiveSessions
        );
    }
    
    /**
     * Session统计信息
     */
    public static class SessionStats {
        private final int totalSessions;
        private final int validSessions;
        private final int expiredSessions;
        private final int maxActiveSessions;
        
        public SessionStats(int totalSessions, int validSessions, int expiredSessions, int maxActiveSessions) {
            this.totalSessions = totalSessions;
            this.validSessions = validSessions;
            this.expiredSessions = expiredSessions;
            this.maxActiveSessions = maxActiveSessions;
        }
        
        // Getters
        public int getTotalSessions() { return totalSessions; }
        public int getValidSessions() { return validSessions; }
        public int getExpiredSessions() { return expiredSessions; }
        public int getMaxActiveSessions() { return maxActiveSessions; }
        
        @Override
        public String toString() {
            return String.format(
                "SessionStats{total=%d, valid=%d, expired=%d, maxActive=%d}",
                totalSessions, validSessions, expiredSessions, maxActiveSessions
            );
        }
    }
}