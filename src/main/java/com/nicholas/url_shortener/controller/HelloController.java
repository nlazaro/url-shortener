package com.nicholas.url_shortener.controller;

import com.nicholas.url_shortener.service.UrlService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

public class HelloController {
    @GetMapping("/hello") // "url/hello"
    public String sayHello(){
        return "Hello World! Your spring boot server is live!";
    }
}