package com.nicholas.url_shortener.controller;

import com.nicholas.url_shortener.service.UrlService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // Tells spring this class handles json/text web responses
public class UrlController { 
    private final UrlService urlService;
    
    public UrlController(UrlService urlService){
        this.urlService = urlService;
    }
    @GetMapping("/test-encode")
    public String testEncode(@RequestParam long id){
        return "the short code for ID" + id + " is: " + urlService.encode(id);
    }
}
