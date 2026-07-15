package com.example.Blink.resource.service;

import com.example.Blink.blocked_url.service.BlockedUrlService;
import com.example.Blink.exception.*;
import com.example.Blink.resource.dto.CreateResourceRequest;
import com.example.Blink.resource.dto.ResourceResponse;
import com.example.Blink.resource.dto.UpdateResourceRequest;
import com.example.Blink.resource.entity.Resource;
import com.example.Blink.resource.mapper.ResourceMapper;
import com.example.Blink.resource.repository.ResourceRepository;
import com.example.Blink.url_click.entity.SourceType;
import com.example.Blink.url_click.service.UrlClickService;
import com.example.Blink.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final PasswordEncoder passwordEncoder;
    private final UrlClickService urlClickService;
    private final BlockedUrlService blockedUrlService;


    @Transactional
    public Resource createResource(CreateResourceRequest request, User user) {

        validateResourceUrl(request.getDestinationUrl());
        Resource resource = resourceMapper.toEntity(request);

        resource.setUser(user);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            resource.setPasswordProtected(true);
            resource.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        } else {
            resource.setPasswordProtected(false);
        }
        return resourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    public Resource getResourceById(UUID resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(ResourceNotFoundException::new);
    }


    @Transactional
    public ResourceResponse updateResource(UUID resourceId, UpdateResourceRequest request) {
        Resource resource =  resourceRepository.findById(resourceId)
                .orElseThrow(ResourceNotFoundException::new);

        resource.setTitle(request.getTitle());
        resource.setExpireAt(request.getExpireAt());

        if (request.getActive() != null) {
            resource.setActive(request.getActive());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            resource.setPasswordProtected(true);
            resource.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        Resource updatedResource = resourceRepository.save(resource);
        return resourceMapper.toResponse(updatedResource);
    }


    @Transactional
    public String resolveScan(UUID resourceId, HttpServletRequest request) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(ResourceNotFoundException::new);

        if (!resource.isActive()) {
            throw new UrlNotActiveException();
        }
        if (resource.getExpireAt() != null && resource.getExpireAt().isBefore(Instant.now())) {
            throw new UrlExpiredException();
        }
        if (resource.isPasswordProtected()) {
            throw new UrlLockedException();
        }

        urlClickService.trackClick(resource, SourceType.QR_CODE, request);
        return resource.getDestinationUrl();
    }

    public void validateResourceUrl(String url) {
        if (blockedUrlService.isDomainBlocked(url)) {
            throw new DomainAlreadyBlockedException();
        }
        try {
            URI uri = URI.create(url);
            if (!List.of("http", "https").contains(uri.getScheme().toLowerCase())) {
                throw new InvalidUrlException();
            }
        } catch (Exception e) {
            throw new InvalidUrlException();
        }
    }
}