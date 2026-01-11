package com.nicholas.url_shortener.controller;

import com.nicholas.url_shortener.dto.UrlRequest;
import com.nicholas.url_shortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @GetMapping("/{shortCode}")
    public void redirectToFullUrl(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        String fullUrl = urlService.getFullUrl(shortCode);
        if (fullUrl != null) { response.sendRedirect(fullUrl); }
        else { response.sendError(HttpServletResponse.SC_NOT_FOUND, "URL not found"); }


    }

    @PostMapping("/shorten")
    public String shortenUrl(@Valid @RequestBody UrlRequest request){
        // takes a string from the request body, and passes it onto the service
        // also accesses teh data via request.getUrl()
        return urlService.shortenURL(request.getUrl());
    }
}

