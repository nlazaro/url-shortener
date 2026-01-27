package com.nicholas.url_shortener.controller;

import com.nicholas.url_shortener.dto.UrlRequest;
import com.nicholas.url_shortener.service.UrlService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController // Tells spring this class handles json/text web responses
public class UrlController { 
    private final UrlService urlService;
    
    public UrlController(UrlService urlService){
        this.urlService = urlService;
    }

//    @GetMapping("/test-encode")
//    public String testEncode(@RequestParam long id){
//        return "the short code for ID" + id + " is: " + urlService.encode(id);
//    }

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String longUrl = urlService.getFullUrl(shortCode);

        if (longUrl == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND) // 302 Redirect
                .location(URI.create(longUrl))
                .build();
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@Valid @RequestBody UrlRequest request, HttpServletRequest servletRequest) {
        String shortCode = urlService.shortenURL(request.getUrl());
        return new ResponseEntity<>(shortCode, HttpStatus.CREATED);
    }
}

