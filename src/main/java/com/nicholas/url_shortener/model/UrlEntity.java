package com.nicholas.url_shortener.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity // tells jpa this class is a database table
@Table(name = "urls")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    private Long id;

    @Column(nullable = false)
    private String fullUrl;

    @Column(nullable = false, unique = true)
    private String shortCode;

    private LocalDateTime CreatedAt;

    public UrlEntity(String fullUrl, String shortCode) {
        if (fullUrl == null || shortCode == null) {
            throw new IllegalArgumentException();
        }
        this.fullUrl = fullUrl;
        this.shortCode = shortCode;
        this.CreatedAt = LocalDateTime.now();
    }
}
