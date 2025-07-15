package com.example.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

@RestController
public class OAuth2Controller {

    private String githubAuth = "https://github.com/login/oauth/authorize?client_id={0}&redirect_uri={1}";

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;

    @GetMapping("/auth/github")
    public String auth(@PathVariable String scene) {
        String authUrl = MessageFormat.format(githubAuth, clientId, "http://localhost:8080/callback");
        return "redirect:" + authUrl;
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code) {

    }
}
