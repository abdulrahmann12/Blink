package com.example.Blink.url_click.entity;

import com.example.Blink.resource.entity.Resource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "resource_clicks",
        indexes = {
                @Index(name = "idx_visit_resource", columnList = "resource_id"),
                @Index(name = "idx_visit_time", columnList = "visited_at"),
                @Index(name = "idx_visit_country", columnList = "country")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceClick {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clickId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 100)
    private String browser;

    @Column(length = 100)
    private String operatingSystem;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Column(length = 100)
    private String country;

    @Column(length = 500)
    private String referrer;

    @Column(name = "visited_at", nullable = false)
    private Instant visitedAt;
}
