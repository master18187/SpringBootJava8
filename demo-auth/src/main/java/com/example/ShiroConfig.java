package com.example;

import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    /**
     * 1. 创建 Realm（数据源）
     */
    @Bean
    public UserRealm userRealm() {
        UserRealm realm = new UserRealm();
        realm.setAuthenticationCachingEnabled(true); // 开启认证缓存
        realm.setAuthorizationCachingEnabled(true); // 开启授权缓存
        return realm;
    }

    /**
     * 2. 创建 SecurityManager（安全管理器）
     */
    @Bean
    public DefaultWebSecurityManager securityManager(UserRealm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    /**
     * 3. 创建 Shiro 过滤器工厂
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);

        // 设置登录页面
        factoryBean.setLoginUrl("/auth/login");
        factoryBean.setUnauthorizedUrl("/auth/unauthorized");

        // 配置访问规则（重要！）
        Map<String, String> filterChainMap = new LinkedHashMap<>();

        // 公开资源（无需登录）
        filterChainMap.put("/auth/**", "anon");     // 认证相关
        filterChainMap.put("/css/**", "anon");       // 静态资源
        filterChainMap.put("/js/**", "anon");
        filterChainMap.put("/images/**", "anon");
        filterChainMap.put("/api/public/**", "anon"); // 公开API

        // 需要认证的资源
        filterChainMap.put("/user/**", "authc");     // 需要认证
        filterChainMap.put("/admin/**", "roles[admin]"); // 需要admin角色
        filterChainMap.put("/api/secure/**", "authc"); // API需要认证

        // 其余所有请求需要认证
        filterChainMap.put("/**", "authc");

        factoryBean.setFilterChainDefinitionMap(filterChainMap);
        return factoryBean;
    }

    /**
     * 4. RememberMe 功能
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.setCookie(rememberMeCookie());
        return rememberMeManager;
    }

    @Bean
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7天
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
}
