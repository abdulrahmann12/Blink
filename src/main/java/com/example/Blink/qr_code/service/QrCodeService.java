package com.example.Blink.qr_code.service;

import com.example.Blink.common.service.ImageService;
import com.example.Blink.exception.*;
import com.example.Blink.common.dto.ImageUploadResult;
import com.example.Blink.qr_code.dto.QrCodeResponse;
import com.example.Blink.qr_code.entity.QrCode;
import com.example.Blink.qr_code.mapper.QrCodeMapper;
import com.example.Blink.qr_code.repository.QrCodeRepository;
import com.example.Blink.resource.dto.CreateResourceRequest;
import com.example.Blink.resource.entity.Resource;
import com.example.Blink.resource.service.ResourceService;
import com.example.Blink.security.AuthenticatedUserService;
import com.example.Blink.url.entity.Url;
import com.example.Blink.url.repository.UrlRepository;
import com.example.Blink.user.entity.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
public class QrCodeService {
    private final QrCodeRepository qrCodeRepository;
    private final ImageService imageService;
    private final QrCodeMapper qrCodeMapper;
    private final AuthenticatedUserService authenticatedUserService;
    private final QrConverterService qrConverterService;
    private final UrlRepository urlRepository;
    private final ResourceService resourceService;

    @Value("${app.base-url}")
    private String baseUrl;


    @Transactional
    @CacheEvict(value = "qrCodes", allEntries = true)
    public QrCodeResponse generateIndependentQrCode(@Valid CreateResourceRequest request){

        User currentUser = authenticatedUserService.getCurrentUser();

        Resource resource = resourceService.createResource(request, currentUser);

        String scanUrl = baseUrl + "scan/" + resource.getResourceId();

        byte[] qrCodeImage = qrConverterService.generateQrCode(scanUrl);
        ImageUploadResult uploadResult = imageService.uploadImage(qrCodeImage);

        QrCode qrCode;
        try {
            qrCode = qrCodeRepository.save(QrCode.builder()
                    .resource(resource)
                    .url(null)
                    .imagePath(uploadResult.imageUrl())
                    .publicId(uploadResult.publicId())
                    .qrText(scanUrl)
                    .active(true)
                    .build());
        } catch (DataIntegrityViolationException ex) {
            imageService.deleteImage(uploadResult.publicId());
            throw new QrCodeAlreadyExistsException();
        }

        return qrCodeMapper.toResponse(qrCode);
    }

    @Transactional
    @CacheEvict(value = "qrCodes", allEntries = true)
    public QrCodeResponse generateQrCode(UUID urlId){
        User currentUser = authenticatedUserService.getCurrentUser();

        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);

        if (!url.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }

        if(qrCodeRepository.existsByUrl_urlId(urlId)){
            throw  new QrCodeAlreadyExistsException();
        }
        if(!url.isActive()){
            throw new UrlNotActiveException();
        }
        if (url.getExpireAt() != null && url.getExpireAt().isBefore(Instant.now())){
            throw new UrlExpiredException();
        }

        byte[] qrCodeImage = qrConverterService.generateQrCode(url.getShortUrl());
        ImageUploadResult uploadResult  = imageService.uploadImage(qrCodeImage);
        QrCode qrCode;
        try {
            qrCode = qrCodeRepository.save(QrCode.builder()
                    .url(url)
                    .imagePath(uploadResult.imageUrl())
                    .publicId(uploadResult.publicId())
                    .qrText(url.getShortUrl())
                    .build());
        }catch (DataIntegrityViolationException ex) {

            imageService.deleteImage(uploadResult.publicId());

            throw new QrCodeAlreadyExistsException();
        }
        return qrCodeMapper.toResponse(qrCode);
    }

    @Cacheable(value = "qrCodes", key = "#p0")
    public QrCodeResponse getQrCode(UUID urlId){
        Url url = urlRepository.findById(urlId)
                .orElseThrow(UrlNotFoundException::new);
        if(!url.isActive()){
            throw new UrlNotActiveException();
        }
        if (url.getExpireAt() != null && url.getExpireAt().isBefore(Instant.now())){
            throw new UrlExpiredException();
        }
        QrCode qrCode = qrCodeRepository.findByUrl_urlId(urlId)
                .orElseThrow(QrCodeNotFoundException::new);

        return qrCodeMapper.toResponse(qrCode);
    }

    @Transactional
    @CacheEvict(value = "qrCodes", allEntries = true)
    public void deleteQrCode(UUID qrCodeId){
        User currentUser = authenticatedUserService.getCurrentUser();

        QrCode qrCode = qrCodeRepository.findById(qrCodeId)
                .orElseThrow(QrCodeNotFoundException::new);

        if (!qrCode.getResource().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }
        imageService.deleteImage(qrCode.getPublicId());
        qrCodeRepository.delete(qrCode);
    }
}
