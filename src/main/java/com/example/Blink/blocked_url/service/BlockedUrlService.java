package com.example.Blink.blocked_url.service;

import com.example.Blink.blocked_url.dto.BlockedUrlResponse;
import com.example.Blink.blocked_url.dto.CreateBlockedUrlRequest;
import com.example.Blink.blocked_url.dto.UpdateBlockedUrlRequest;
import com.example.Blink.blocked_url.entity.BlockedUrl;
import com.example.Blink.blocked_url.mapper.BlockedUrlMapper;
import com.example.Blink.blocked_url.repository.BlockedUrlRepository;
import com.example.Blink.exception.BlockedUrlNotFoundException;
import com.example.Blink.exception.DomainAlreadyBlockedException;
import com.example.Blink.exception.DomainEmptyException;
import com.example.Blink.exception.DomainNotInBlockedListException;
import com.example.Blink.exception.InvalidDomainException;
import com.example.Blink.exception.InvalidDomainFormatException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Service
@Validated
@RequiredArgsConstructor
public class BlockedUrlService {
    private final BlockedUrlRepository blockedUrlRepository;
    private final BlockedUrlMapper blockedUrlMapper;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "blockedUrls", allEntries = true),
            @CacheEvict(value = "blockedDomains", allEntries = true)
    })
    public BlockedUrlResponse blockDomain(@Valid CreateBlockedUrlRequest request){
        String domain = normalizeDomain(request.getDomain());
        if(blockedUrlRepository.existsByDomain(domain)){
            throw new DomainAlreadyBlockedException();
        }
        BlockedUrl blockedUrl = blockedUrlMapper.toEntity(request);
        blockedUrl.setDomain(domain);
        BlockedUrl savedBlockedUrl = blockedUrlRepository.save(blockedUrl);
        return blockedUrlMapper.toBlockedUrlResponse(savedBlockedUrl);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "blockedUrls", allEntries = true),
            @CacheEvict(value = "blockedDomains", allEntries = true)
    })
    public BlockedUrlResponse updateBlockedUrl(Long blockedUrlId, @Valid UpdateBlockedUrlRequest updateBlockedUrlRequest){
        BlockedUrl blockedUrl = blockedUrlRepository.findById(blockedUrlId)
                .orElseThrow(BlockedUrlNotFoundException::new);
        blockedUrl.setReason(updateBlockedUrlRequest.getReason());
        BlockedUrl updatedBlockedUrl = blockedUrlRepository.save(blockedUrl);
        return blockedUrlMapper.toBlockedUrlResponse(updatedBlockedUrl);
    }

    @Cacheable(value = "blockedUrls", key = "#p0")
    public BlockedUrlResponse getBlockedUrlById(Long blockedUrlId){
        BlockedUrl blockedUrl = blockedUrlRepository.findById(blockedUrlId)
                .orElseThrow(BlockedUrlNotFoundException::new);
        return blockedUrlMapper.toBlockedUrlResponse(blockedUrl);
    }

    public Page<BlockedUrlResponse> getBlockedUrls(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<BlockedUrl> blockedUrls = blockedUrlRepository.findAll(pageable);
        return blockedUrls.map(blockedUrlMapper::toBlockedUrlResponse);
    }

    @Cacheable(value = "blockedDomains",key = "#root.target.normalizeDomain(#domain)")
    public boolean isDomainBlocked(String domain){
        return blockedUrlRepository.existsByDomain(normalizeDomain(domain));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "blockedUrls", allEntries = true),
            @CacheEvict(value = "blockedDomains", allEntries = true)
    })
    public void unblockDomain(Long blockedUrlId){
        BlockedUrl blockedUrl = blockedUrlRepository.findById(blockedUrlId)
                .orElseThrow(DomainNotInBlockedListException::new);
        blockedUrlRepository.delete(blockedUrl);
    }

    public String normalizeDomain(String input) {
        if (input == null || input.isBlank()) {
            throw new DomainEmptyException();
        }

        String value = input.trim().toLowerCase();

        try {
            // لو المستخدم بعت example.com بدون protocol
            if (!value.startsWith("http://") && !value.startsWith("https://")) {
                value = "https://" + value;
            }

            URI uri = URI.create(value);

            String host = uri.getHost();

            if (host == null || host.isBlank()) {
                throw new InvalidDomainException();
            }

            if (host.startsWith("www.")) {
                host = host.substring(4);
            }

            return host;

        } catch (Exception e) {
            throw new InvalidDomainFormatException();
        }
    }
}
