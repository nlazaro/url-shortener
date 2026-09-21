package com.nicholas.url_shortener.service;

import com.nicholas.url_shortener.exception.UrlNotFoundException;
import com.nicholas.url_shortener.model.UrlEntity;
import com.nicholas.url_shortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
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

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // ---------- shortenURL ----------

    @Test
    void shortenURL_returns7CharCode_savesItAndWritesToCache() {
        String code = urlService.shortenURL("https://google.com");

        assertThat(code).hasSize(UrlService.CODE_LENGTH).matches("[a-zA-Z0-9]+");
        verify(repository, times(1)).saveAndFlush(any(UrlEntity.class));
        verify(valueOperations).set(code, "https://google.com", 1L, TimeUnit.DAYS);
    }

    @Test
    void shortenURL_retriesWithNewCode_whenShortCodeCollides() {
        when(repository.saveAndFlush(any(UrlEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate shortCode"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String code = urlService.shortenURL("https://google.com");

        assertThat(code).hasSize(UrlService.CODE_LENGTH);
        verify(repository, times(2)).saveAndFlush(any(UrlEntity.class));
        verify(valueOperations, times(1)).set(eq(code), eq("https://google.com"), anyLong(), any(TimeUnit.class));
    }

    @Test
    void shortenURL_givesUp_afterMaxAttempts() {
        when(repository.saveAndFlush(any(UrlEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate shortCode"));

        assertThatThrownBy(() -> urlService.shortenURL("https://google.com"))
                .isInstanceOf(IllegalStateException.class);
        verify(repository, times(UrlService.MAX_ATTEMPTS)).saveAndFlush(any(UrlEntity.class));
        verifyNoInteractions(valueOperations);
    }

    // ---------- getFullUrl ----------

    @Test
    void getFullUrl_cacheHit_neverQueriesDatabase() {
        when(valueOperations.get("abc1234")).thenReturn("https://google.com");

        String url = urlService.getFullUrl("abc1234");

        assertThat(url).isEqualTo("https://google.com");
        verifyNoInteractions(repository);
    }

    @Test
    void getFullUrl_cacheMiss_readsDatabaseAndPopulatesCache() {
        when(valueOperations.get("abc1234")).thenReturn(null);
        when(repository.findByShortCode("abc1234"))
                .thenReturn(Optional.of(new UrlEntity("https://google.com", "abc1234")));

        String url = urlService.getFullUrl("abc1234");

        assertThat(url).isEqualTo("https://google.com");
        verify(repository, times(1)).findByShortCode("abc1234");
        verify(valueOperations).set("abc1234", "https://google.com", 1L, TimeUnit.DAYS);
    }

    @Test
    void getFullUrl_unknownCode_throwsAndDoesNotCache() {
        when(valueOperations.get("missing")).thenReturn(null);
        when(repository.findByShortCode("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getFullUrl("missing"))
                .isInstanceOf(UrlNotFoundException.class);
        verify(valueOperations, never()).set(any(), any(), anyLong(), any(TimeUnit.class));
    }
}
