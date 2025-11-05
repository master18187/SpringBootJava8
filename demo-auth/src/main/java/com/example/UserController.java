package com.example;

import org.apache.shiro.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 需要认证才能访问
     */
    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        // UserProfile profile = userService.getUserProfile(username);
        return ResponseEntity.ok("profile");
    }
}

