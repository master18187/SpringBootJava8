package com.example;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.*;

@Configuration
public class ShiroConfig {

    @Value("${shiro.login.url:/sign_in}")
    private String loginUrl;
    
    @Value("${shiro.unauthorized.url:/auth/unauthorized}")
    private String unauthorizedUrl;
    
    @Value("${shiro.rememberMe.maxAge:604800}")
    private int rememberMeMaxAge;
    
    @Value("${shiro.session.timeout:3600}")
    private int sessionTimeout;
    
    @Value("${shiro.cookie.name:rememberMe}")
    private String cookieName;

    /**
     * 1. 创建 Realm（数据源）
     */
    @Bean
    public UserRealm userRealm(HashedCredentialsMatcher credentialsMatcher) {
        UserRealm realm = new UserRealm();
        realm.setCredentialsMatcher(credentialsMatcher);
        realm.setAuthenticationCachingEnabled(true);
        realm.setAuthorizationCachingEnabled(true);
        realm.setAuthenticationCacheName("authenticationCache");
        realm.setAuthorizationCacheName("authorizationCache");
        return realm;
    }

    /**
     * 凭证匹配器：配置密码加密策略
     */
    @Bean
    public HashedCredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("SHA-256");
        matcher.setHashIterations(1024);
        matcher.setStoredCredentialsHexEncoded(true);
        return matcher;
    }

/**
     * 2. 创建 SecurityManager（安全管理器）
     */
    @Bean
    public DefaultWebSecurityManager securityManager(UserRealm realm, 
                                                   CookieRememberMeManager rememberMeManager,
                                                   org.apache.shiro.web.session.mgt.DefaultWebSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setRememberMeManager(rememberMeManager);
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        
        // 设置会话管理器
        securityManager.setSessionManager(sessionManager);
        
        return securityManager;
    }

/**
     * 会话管理器配置
     */
    @Bean
    public org.apache.shiro.web.session.mgt.DefaultWebSessionManager sessionManager(CustomSessionDAO customSessionDAO) {
        org.apache.shiro.web.session.mgt.DefaultWebSessionManager sessionManager = 
            new org.apache.shiro.web.session.mgt.DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(sessionTimeout * 1000);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionValidationInterval(60000); // 每分钟检查一次
        
        // 配置Session DAO
        sessionManager.setSessionDAO(customSessionDAO);
        
        // 配置Session ID生成器
        // sessionManager.setSessionIdGenerator(new org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator());
        
        return sessionManager;
    }

    /**
     * 3. 创建 Shiro 过滤器工厂
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        
        // 设置登录和未授权页面
        factoryBean.setLoginUrl(loginUrl);
        factoryBean.setUnauthorizedUrl(unauthorizedUrl);

        // 配置访问规则
        Map<String, String> filterChainMap = buildFilterChainDefinitionMap();
        
        // 配置自定义过滤器（如果需要）
        Map<String, Filter> filters = new LinkedHashMap<>();
        // filters.put("authc", new CustomAuthenticationFilter());
        factoryBean.setFilters(filters);

        factoryBean.setFilterChainDefinitionMap(filterChainMap);
        return factoryBean;
    }

    /**
     * 构建过滤器链定义映射
     */
    private Map<String, String> buildFilterChainDefinitionMap() {
        Map<String, String> filterChainMap = new LinkedHashMap<>();

        // 公开资源（无需登录）
        filterChainMap.put("/static/**", "anon");
        filterChainMap.put("/css/**", "anon");
        filterChainMap.put("/js/**", "anon");
        filterChainMap.put("/images/**", "anon");
        filterChainMap.put("/fonts/**", "anon");
        filterChainMap.put("/api/public/**", "anon");
        filterChainMap.put("/sign_in", "anon");
        filterChainMap.put("/auth/login", "anon");
        filterChainMap.put("/auth/logout", "logout");
        
        // 健康检查端点
        filterChainMap.put("/health", "anon");
        filterChainMap.put("/actuator/**", "anon");

        // 需要认证的资源
        filterChainMap.put("/user/**", "authc");
        filterChainMap.put("/profile/**", "authc");
        filterChainMap.put("/settings/**", "authc");
        
        // 需要特定角色的资源
        filterChainMap.put("/admin/**", "roles[admin]");
        filterChainMap.put("/moderator/**", "roles[admin,moderator]");
        
        // 需要特定权限的资源
        filterChainMap.put("/api/secure/**", "authc");
        filterChainMap.put("/api/admin/**", "roles[admin]");
        filterChainMap.put("/write/**", "perms[write]");
        filterChainMap.put("/delete/**", "perms[delete]");

        // WebSocket支持
        filterChainMap.put("/ws/**", "anon");

        // 其余所有请求需要认证
        filterChainMap.put("/**", "authc");

        return filterChainMap;
    }

    /**
     * 4. RememberMe 功能配置
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.setCookie(rememberMeCookie());
        
        // 设置加密密钥（生产环境应该从配置文件读取）
        rememberMeManager.setCipherKey(org.apache.shiro.codec.Base64.decode(
            "kPH+bIxk5D2deZiIxcaaaA=="));
        
        return rememberMeManager;
    }

    @Bean
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie(cookieName);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 生产环境应设置为true（HTTPS）
        cookie.setMaxAge(rememberMeMaxAge);
        cookie.setPath("/");
        cookie.setComment("Remember Me Cookie");
        return cookie;
    }

/**
     * 5. 开启Shiro注解支持
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

/**
     * 6. Shiro生命周期处理器
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
