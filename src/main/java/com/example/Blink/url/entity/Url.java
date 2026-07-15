package com.example.Blink.url.entity;

import com.example.Blink.resource.entity.Resource;
import com.example.Blink.url_click.entity.UrlClick;
import com.example.Blink.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "Urls",
        indexes = {
                @Index(name = "idx_url_short_url", columnList = "short_url"),
                @Index(name = "idx_url_custom_alias", columnList = "custom_alias"),
                @Index(name = "idx_click_count", columnList = "click_count"),
                @Index(name = "idx_expire_at", columnList = "expire_at"),
                @Index(name = "idx_active", columnList = "active")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID urlId;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(unique = true)
    private String shortUrl;

    private String title;

    @Column(unique = true, length = 40)
    @Pattern(regexp = "^[A-Za-z0-9]{7,40}$")
    private String customAlias;

    private boolean passwordProtected;

    private String passwordHash;

    @Future
    private Instant expireAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private boolean active;

    private long clickCount;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant deletesAt;
}
