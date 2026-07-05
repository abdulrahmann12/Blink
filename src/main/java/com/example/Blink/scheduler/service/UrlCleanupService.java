package com.example.Blink.scheduler.service;

import com.example.Blink.common.service.ImageService;
import com.example.Blink.qr_code.entity.QrCode;
import com.example.Blink.qr_code.repository.QrCodeRepository;
import com.example.Blink.url.entity.Url;
import com.example.Blink.url.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlCleanupService {

    private final UrlRepository urlRepository;
    private final QrCodeRepository qrCodeRepository;
    private final ImageService imageService;

    public void removeExpiredUrls() {

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<Url> urls = urlRepository.findByExpireAtBefore(oneMonthAgo);

        log.info("Found {} expired URLs to clean up.", urls.size());

        for (Url url : urls) {
            try {

                QrCode qrCode = qrCodeRepository
                        .findByUrl_urlId(url.getUrlId())
                        .orElse(null);

                if (qrCode != null) {
                    imageService.deleteImage(qrCode.getPublicId());
                }

                deleteUrlData(url, qrCode);

                log.info("Deleted expired URL: {}", url.getUrlId());

            } catch (Exception e) {
                log.error("Failed to delete expired URL: {}", url.getUrlId(), e);
            }
        }
    }

    @Transactional
    public void deleteUrlData(Url url, QrCode qrCode) {

        if (qrCode != null) {
            qrCodeRepository.delete(qrCode);
        }
        urlRepository.delete(url);
    }
}