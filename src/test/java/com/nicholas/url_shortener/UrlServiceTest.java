package com.nicholas.url_shortener;

import com.nicholas.url_shortener.repository.UrlRepository;
import com.nicholas.url_shortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest{
    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    void testEndToEndConversion(){
        long originalId = 999999L;
        String shortCode = urlService.encode(originalId);
        long decodedId = urlService.decode(shortCode);

        assertEquals(originalId, decodedId, "The decoded ID must match the original ID");
    }
}