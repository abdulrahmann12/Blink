package com.example.Blink.blocked_url.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "blocked_urls")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BlockedUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockedUrlId;

    @Column(nullable = false, unique = true)
    private String domain;

    private String reason;

    @CreationTimestamp
    private Instant blockedAt;
}
