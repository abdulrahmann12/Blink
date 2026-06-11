package com.example.Blink.qr_code.repository;

import com.example.Blink.qr_code.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {

    Optional<QrCode> findByUrl_urlId(UUID urlId);

    boolean existsByUrl_urlId(UUID urlId);
}
