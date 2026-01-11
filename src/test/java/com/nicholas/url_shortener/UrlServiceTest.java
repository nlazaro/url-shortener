package com.nicholas.url_shortener;

import com.nicholas.url_shortener.service.UrlService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlServiceTest{
    private final UrlService urlService = new UrlService();
    @Test
    void testEndToEndConversion(){
        long originalId = 999999L;
        String shortCode = urlService.encode(originalId);
        long decodedId = urlService.decode(shortCode);

        assertEquals(originalId, decodedId, "The decoded ID must match the original ID");
    }
}