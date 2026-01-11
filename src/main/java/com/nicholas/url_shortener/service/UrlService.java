package com.nicholas.url_shortener.service;

import com.nicholas.url_shortener.exception.UrlNotFoundException;
import com.nicholas.url_shortener.model.UrlEntity;
import com.nicholas.url_shortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

@Service
public class UrlService{
    private final UrlRepository repository;
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = ALLOWED_CHARS.length();

    // converts a unique database ID into a base62 string
    public String encode(long input){
        StringBuilder encodedString = new StringBuilder();
        if (input == 0) return String.valueOf(ALLOWED_CHARS.charAt(0));
        while (input > 0){
            encodedString.append(ALLOWED_CHARS.charAt((int) (input % BASE)));
            input = input / BASE;
        }
        return encodedString.reverse().toString();
    }

    // converts a base62 string back into a database ID
    public long decode(String input){
        long decoded = 0;
        for (int i = 0; i < input.length(); i++){
            decoded = decoded * BASE + ALLOWED_CHARS.indexOf(input.charAt(i));
        }
        return decoded;
    }

    public UrlService(UrlRepository repository){
        this.repository = repository;
    }

    public String shortenURL(String longUrl){
        // creates the entity object
        UrlEntity entity = new UrlEntity();
        entity.setFullUrl(longUrl);

        // saves to database. hibernate generates "insert into urls"
        // id is generated and returned to object
        entity = repository.save(entity);

        // use the generated id to create the short code
        return encode(entity.getId());
    }

    public String getFullUrl(String shortCode) {
        long id = decode(shortCode);
        return repository.findById(id)
                .map(UrlEntity::getFullUrl)
                .orElseThrow(() -> new UrlNotFoundException("Short code '" + shortCode + "' does not exist"));
    }
}