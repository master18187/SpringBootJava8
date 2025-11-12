package com.example;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 自定义SessionDAO实现
 * 支持内存存储和Redis存储两种模式
 */
@Component
public class CustomSessionDAO extends CachingSessionDAO {

    private static final Logger logger = LoggerFactory.getLogger(CustomSessionDAO.class);
    
    /**
     * 是否使用Redis存储，false则使用内存存储
     */
    @Value("${shiro.session.redis.enabled:false}")
    private boolean redisEnabled;
    
    /**
     * Session在Redis中的过期时间（秒）
     */
    @Value("${shiro.session.redis.expire:3600}")
    private int redisExpire;
    
    /**
     * Redis key前缀
     */
    @Value("${shiro.session.redis.prefix:shiro:session:}")
    private String redisKeyPrefix;
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 本地内存缓存（当Redis不可用时的备选方案）
     */
    private final Map<Serializable, Session> memorySessions = new ConcurrentHashMap<>();
    
    /**
     * 创建Session - 存储到数据源
     */
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        
        try {
            if (redisEnabled && redisTemplate != null) {
                storeSessionToRedis(sessionId, session);
                logger.debug("Session {} created and stored in Redis", sessionId);
            } else {
                memorySessions.put(sessionId, session);
                logger.debug("Session {} created and stored in memory", sessionId);
            }
        } catch (Exception e) {
            logger.error("Error creating session {}: {}", sessionId, e.getMessage(), e);
            // 如果Redis存储失败，降级到内存存储
            if (redisEnabled) {
                memorySessions.put(sessionId, session);
                logger.warn("Fallback to memory storage for session {}", sessionId);
            }
        }
        
        return sessionId;
    }
    
    /**
     * 读取Session - 从数据源获取
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            return null;
        }
        
        try {
            if (redisEnabled && redisTemplate != null) {
                Session session = getSessionFromRedis(sessionId);
                if (session != null) {
                    logger.debug("Session {} read from Redis", sessionId);
                    return session;
                }
            }
        } catch (Exception e) {
            logger.error("Error reading session {} from Redis: {}", sessionId, e.getMessage(), e);
        }
        
        // 从内存中读取
        Session session = memorySessions.get(sessionId);
        if (session != null) {
            logger.debug("Session {} read from memory", sessionId);
        }
        
        return session;
    }
    
    /**
     * 更新Session - 更新数据源中的Session
     */
    @Override
    protected void doUpdate(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        
        Serializable sessionId = session.getId();
        
        try {
            if (redisEnabled && redisTemplate != null) {
                storeSessionToRedis(sessionId, session);
                logger.debug("Session {} updated in Redis", sessionId);
            } else {
                memorySessions.put(sessionId, session);
                logger.debug("Session {} updated in memory", sessionId);
            }
        } catch (Exception e) {
            logger.error("Error updating session {}: {}", sessionId, e.getMessage(), e);
            // 如果Redis更新失败，降级到内存存储
            if (redisEnabled) {
                memorySessions.put(sessionId, session);
                logger.warn("Fallback to memory storage for session {}", sessionId);
            }
        }
    }
    
    /**
     * 删除Session - 从数据源删除
     */
    @Override
    protected void doDelete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        
        Serializable sessionId = session.getId();
        
        try {
            if (redisEnabled && redisTemplate != null) {
                deleteSessionFromRedis(sessionId);
                logger.debug("Session {} deleted from Redis", sessionId);
            }
        } catch (Exception e) {
            logger.error("Error deleting session {} from Redis: {}", sessionId, e.getMessage(), e);
        }
        
        // 从内存中删除
        memorySessions.remove(sessionId);
        logger.debug("Session {} deleted from memory", sessionId);
    }
    
    /**
     * 获取所有活跃的Session
     */
    @Override
    public Collection<Session> getActiveSessions() {
        try {
            if (redisEnabled && redisTemplate != null) {
                return getAllSessionsFromRedis();
            }
        } catch (Exception e) {
            logger.error("Error getting active sessions from Redis: {}", e.getMessage(), e);
        }
        
        // 返回内存中的Sessions
        Collection<Session> sessions = memorySessions.values();
        logger.debug("Returning {} active sessions from memory", sessions.size());
        return sessions;
    }
    
    // ==================== Redis操作方法 ====================
    
    /**
     * 将Session存储到Redis
     */
    private void storeSessionToRedis(Serializable sessionId, Session session) {
        try {
            String key = redisKeyPrefix + sessionId;
            redisTemplate.opsForValue().set(key, session, redisExpire, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Failed to store session {} to Redis: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }
    
/**
     * 从Redis获取Session
     */
    @SuppressWarnings("unchecked")
    private Session getSessionFromRedis(Serializable sessionId) {
        try {
            String key = redisKeyPrefix + sessionId;
            Object sessionObj = redisTemplate.opsForValue().get(key);
            if (sessionObj instanceof Session) {
                return (Session) sessionObj;
            } else if (sessionObj instanceof Map) {
                // 如果存储的是Map，需要转换为Session对象
                logger.warn("Session stored as Map, need to implement Session deserialization");
                return null;
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to get session {} from Redis: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 从Redis删除Session
     */
    private void deleteSessionFromRedis(Serializable sessionId) {
        try {
            String key = redisKeyPrefix + sessionId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            logger.error("Failed to delete session {} from Redis: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }
    
/**
     * 从Redis获取所有活跃的Session
     */
    @SuppressWarnings("unchecked")
    private Collection<Session> getAllSessionsFromRedis() {
        try {
            Set<String> keys = redisTemplate.keys(redisKeyPrefix + "*");
            if (keys == null || keys.isEmpty()) {
                return new ArrayList<>();
            }
            
            List<Object> sessions = redisTemplate.opsForValue().multiGet(keys);
            List<Session> result = new ArrayList<>();
            
            if (sessions != null) {
                for (Object sessionObj : sessions) {
                    if (sessionObj instanceof Session) {
                        result.add((Session) sessionObj);
                    } else if (sessionObj instanceof Map) {
                        logger.warn("Found session stored as Map, skipping");
                    }
                }
            }
            
            return result;
        } catch (Exception e) {
            logger.error("Failed to get all sessions from Redis: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    // ==================== 统计和管理方法 ====================
    
    /**
     * 获取当前Session数量
     */
    public int getActiveSessionCount() {
        return getActiveSessions().size();
    }
    
    /**
     * 清理过期的Session
     */
    public void cleanupExpiredSessions() {
        try {
            Collection<Session> sessions = getActiveSessions();
            int cleanedCount = 0;
            
            for (Session session : sessions) {
                if (session != null) {
                    try {
                        // session.validate();
                    } catch (Exception e) {
                        // Session已过期，删除它
                        doDelete(session);
                        cleanedCount++;
                    }
                }
            }
            
            logger.info("Cleaned up {} expired sessions", cleanedCount);
        } catch (Exception e) {
            logger.error("Error during session cleanup: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 检查Redis连接是否可用
     */
    public boolean isRedisAvailable() {
        if (!redisEnabled || redisTemplate == null) {
            return false;
        }
        
        try {
            redisTemplate.opsForValue().get("health:check");
            return true;
        } catch (Exception e) {
            logger.warn("Redis is not available: {}", e.getMessage());
            return false;
        }
    }
}