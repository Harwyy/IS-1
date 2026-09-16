package ru.itmo.secureapi.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "secure_data")
public class SecureData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false, length = 64)
    private String owner;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected SecureData() {}

    public SecureData(String title, String content, String owner) {
        this.title = title;
        this.content = content;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getOwner() { return owner; }
    public Instant getCreatedAt() { return createdAt; }
}
