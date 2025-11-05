package com.example;

import lombok.Data;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(
                    request.getUsername(),
                    request.getPassword()
            );

            // 执行登录认证
            subject.login(token);

            result.put("success", true);
            result.put("message", "登录成功");
            result.put("sessionId", subject.getSession().getId());

            return ResponseEntity.ok(result);

        } catch (AuthenticationException e) {
            result.put("success", false);
            result.put("message", "登录失败: " + e.getMessage());
            return ResponseEntity.status(401).body(result);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityUtils.getSubject().logout();
        return ResponseEntity.ok("退出成功");
    }

    @GetMapping("/unauthorized")
    public ResponseEntity<String> unauthorized() {
        return ResponseEntity.status(403).body("没有访问权限");
    }
}

// 登录请求DTO
@Data
class LoginRequest {
    private String username;
    private String password;
}
