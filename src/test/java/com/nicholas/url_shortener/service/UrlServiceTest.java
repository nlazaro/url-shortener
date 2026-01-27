package com.nicholas.url_shortener.service;

import com.nicholas.url_shortener.model.UrlEntity;
import com.nicholas.url_shortener.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository repository;

    @Mock
    private RedisTemplate<Object, Object> redisTemplate;

    @Mock
    private ValueOperations<Object, Object> valueOperations;

    @InjectMocks
    private UrlService urlService;

    @Test
    void shortenURL_ShouldReturn7CharacterCode() {
        // Mock the Redis behavior so it doesn't null pointer
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String code = urlService.shortenURL("https://google.com");

        assertThat(code).hasSize(7);
        verify(repository, times(1)).save(any(UrlEntity.class));
    }
}