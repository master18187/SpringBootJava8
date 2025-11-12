package com.example;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户服务类
 * 提供用户认证和授权相关的业务逻辑
 */
@Service
public class UserService {

    /**
     * 获取用户角色
     * @param username 用户名
     * @return 角色集合
     */
    public Set<String> getUserRoles(String username) {
        Set<String> roles = new HashSet<>();
        
        // 这里应该是从数据库查询用户角色
        // 为了演示，这里使用硬编码的角色
        if ("admin".equals(username)) {
            roles.add("admin");
        } else if ("user".equals(username)) {
            roles.add("user");
        } else {
            // 默认角色
            roles.add("user");
        }
        
        return roles;
    }

    /**
     * 获取用户权限
     * @param username 用户名
     * @return 权限集合
     */
    public Set<String> getUserPermissions(String username) {
        Set<String> permissions = new HashSet<>();
        
        // 这里应该是从数据库查询用户权限
        // 为了演示，这里使用硬编码的权限
        if ("admin".equals(username)) {
            permissions.add("read");
            permissions.add("write");
            permissions.add("delete");
            permissions.add("admin");
        } else {
            // 普通用户权限
            permissions.add("read");
            permissions.add("write");
        }
        
        return permissions;
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    public User findByUsername(String username) {
        // 这里应该是从数据库查询用户信息
        // 为了演示，这里返回一个模拟的用户对象
        User user = new User();
        user.setUsername(username);
        
        if ("admin".equals(username)) {
            user.setPassword("admin123"); // 实际项目中应该是加密后的密码
            user.setEnabled(true);
            user.setLocked(false);
        } else if ("user".equals(username)) {
            user.setPassword("user123");
            user.setEnabled(true);
            user.setLocked(false);
        }
        
        return user;
    }

    /**
     * 用户实体类（内部类）
     */
    public static class User {
        private String username;
        private String password;
        private boolean enabled;
        private boolean locked;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }
    }
}
