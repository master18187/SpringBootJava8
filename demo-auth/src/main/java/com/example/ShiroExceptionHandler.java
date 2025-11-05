package com.example;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ShiroExceptionHandler {

    /**
     * 处理Shiro认证异常
     */
    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<String> handleAuthException(AuthenticationException e) {
        return ResponseEntity.status(401).body("认证失败: " + e.getMessage());
    }

    /**
     * 处理Shiro授权异常
     */
    @ExceptionHandler({AuthorizationException.class})
    public ResponseEntity<String> handleAuthorizationException(AuthorizationException e) {
        return ResponseEntity.status(403).body("权限不足: " + e.getMessage());
    }
}
