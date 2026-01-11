package com.nicholas.url_shortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(UrlNotFoundException ex){
        return new ResponseEntity<>(
                Map.of("error", "Not Found", "message", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
