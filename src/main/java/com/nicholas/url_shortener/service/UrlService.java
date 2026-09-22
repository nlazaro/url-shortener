package com.nicholas.url_shortener.service;

import com.nicholas.url_shortener.exception.UrlNotFoundException;
import com.nicholas.url_shortener.model.UrlEntity;
import com.nicholas.url_shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {
    static final int CODE_LENGTH = 7;
    static final int MAX_ATTEMPTS = 5;
    private static final long CACHE_TTL_DAYS = 1;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final UrlRepository repository;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final boolean cacheEnabled;
    private final SecureRandom random = new SecureRandom();

    public UrlService(UrlRepository repository,
                      RedisTemplate<Object, Object> redisTemplate,
                      @Value("${app.cache.enabled:true}") boolean cacheEnabled) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * Generates a random short code and persists it. The unique constraint on shortCode is the
     * source of truth for collisions: on a violation we retry with a new code, up to MAX_ATTEMPTS.
     */
    public String shortenURL(String longUrl) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String code = generateRandomCode();
            try {
                // saveAndFlush forces the INSERT now, so a collision surfaces inside this try block
                repository.saveAndFlush(new UrlEntity(longUrl, code));
            } catch (DataIntegrityViolationException e) {
                continue; // short code already taken, try another
            }
            cachePut(code, longUrl);
            return code;
        }
        throw new IllegalStateException("Could not generate a unique short code after " + MAX_ATTEMPTS + " attempts");
    }

    /**
     * Cache-aside lookup: check Redis first, fall back to Postgres on a miss, then populate the cache.
     */
    public String getFullUrl(String shortCode) {
        if (cacheEnabled) {
            Object cached = redisTemplate.opsForValue().get(shortCode);
            if (cached != null) {
                return cached.toString();
            }
        }

        String fullUrl = repository.findByShortCode(shortCode)
                .map(UrlEntity::getFullUrl)
                .orElseThrow(() -> new UrlNotFoundException("Short code '" + shortCode + "' does not exist"));

        cachePut(shortCode, fullUrl);
        return fullUrl;
    }

    private void cachePut(String shortCode, String fullUrl) {
        if (cacheEnabled) {
            redisTemplate.opsForValue().set(shortCode, fullUrl, CACHE_TTL_DAYS, TimeUnit.DAYS);
        }
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
