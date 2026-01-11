package com.nicholas.url_shortener.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Tells spring this class handles json/text web responses
public class HelloController {
    @GetMapping("/hello") // "url/hello"
    public String sayHello(){
        return "Hello World! Your spring boot server is live!";
    }
}