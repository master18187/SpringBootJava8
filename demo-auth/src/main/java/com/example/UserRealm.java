package com.example;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 授权逻辑：获取用户的角色和权限
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        // 1. 查询用户角色
        Set<String> roles = userService.getUserRoles(username);
        authorizationInfo.setRoles(roles);

        // 2. 查询用户权限
        Set<String> permissions = userService.getUserPermissions(username);
        authorizationInfo.setStringPermissions(permissions);

        return authorizationInfo;
    }

/**
     * 认证逻辑：验证用户身份
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();

        // 1. 查询用户信息
        UserService.User user = userService.findByUsername(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }

        // 2. 检查账户状态
        if (!user.isEnabled()) {
            throw new DisabledAccountException("账户已被禁用");
        }

        if (user.isLocked()) {
            throw new LockedAccountException("账户已被锁定");
        }

        // 3. 返回认证信息（密码验证由Shiro的CredentialsMatcher处理）
        return new SimpleAuthenticationInfo(
                username,               // principal：用户名
                user.getPassword(),     // credentials：密码（加密后的）
                getName()               // realmName
        );
    }
}
