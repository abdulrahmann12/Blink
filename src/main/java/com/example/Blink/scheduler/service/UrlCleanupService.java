package com.example.Blink.scheduler.service;

import com.example.Blink.common.service.ImageService;
import com.example.Blink.qr_code.repository.QrCodeRepository;
import com.example.Blink.url.entity.Url;
import com.example.Blink.url.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UrlCleanupService {
    private final UrlRepository urlRepository;
    private final QrCodeRepository qrCodeRepository;
    private final ImageService imageService;

    public void removeExpiredUrls(){
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<Url> urls = urlRepository.findByExpireAtBefore(oneMonthAgo);
        for (Url url : urls){
            qrCodeRepository.findByUrl_urlId(url.getUrlId()).ifPresent(
                    qrCode -> {
                        imageService.deleteImage(qrCode.getPublicId());
                        qrCodeRepository.delete(qrCode);
                    });
            urlRepository.delete(url);
        }

    }


}

