package com.nicholas.url_shortener.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity // tells jpa this class is a database table
@Table(name = "urls")
@Data // Lombok: generates getters, setters, and toString automatically

public class UrlEntity {
    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    private Long id;

    @Column(nullable = false)
    private String fullUrl;
    private LocalDateTime CreatedAt;

    // set the data automatically before saving to the database
    @PrePersist
    protected void onCreate() {
        CreatedAt = LocalDateTime.now();
    }
}
