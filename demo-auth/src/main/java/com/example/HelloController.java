package com.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(value = "/", method = RequestMethod.POST)
    String hello() {
        return "Hello World!";
    }


    @RequestMapping("/user")
    String user() {
        return "user!";
    }

}
