package com.example.Blink.qr_code.entity;

import com.example.Blink.resource.entity.Resource;
import com.example.Blink.url.entity.Url;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "qr_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID qrId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = true, unique = true)
    private Url url;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @Column(nullable = false)
    private String imagePath;

    @Builder.Default
    private boolean active = true;

    private String qrText;

    @Column(nullable = false)
    private String publicId;

    @CreationTimestamp
    private Instant createdAt;
}
