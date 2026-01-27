package com.nicholas.url_shortener.service;

import com.nicholas.url_shortener.exception.UrlNotFoundException;
import com.nicholas.url_shortener.model.UrlEntity;
import com.nicholas.url_shortener.repository.UrlRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {
    private final UrlRepository repository;
    private final RedisTemplate<Object, Object> redisTemplate;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SecureRandom random = new SecureRandom();

    public UrlService(UrlRepository repository, RedisTemplate<Object, Object> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    public String shortenURL(String longUrl) {
        String code = generateRandomCode();
        UrlEntity entity = new UrlEntity();
        entity.setFullUrl(longUrl);
        entity.setShortCode(code);
        repository.save(entity);
        redisTemplate.opsForValue().set(code, longUrl, 1, TimeUnit.DAYS);
        return code;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    @Cacheable(value = "urls", key = "#shortCode")
    public String getFullUrl(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(UrlEntity::getFullUrl)
                .orElseThrow(() -> new UrlNotFoundException("Short code '" + shortCode + "' does not exist"));
    }
}