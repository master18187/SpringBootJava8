package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SignInController {

    @RequestMapping(value = "/sign_in")
    public String sign_in() {
        return "signin";
    }
}
