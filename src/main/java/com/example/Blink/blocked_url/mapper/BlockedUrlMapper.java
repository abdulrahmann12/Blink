package com.example.Blink.blocked_url.mapper;

import com.example.Blink.blocked_url.dto.BlockedUrlResponse;
import com.example.Blink.blocked_url.dto.CreateBlockedUrlRequest;
import com.example.Blink.blocked_url.entity.BlockedUrl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlockedUrlMapper {

    BlockedUrlResponse toBlockedUrlResponse(BlockedUrl blockedUrl);

    @Mapping(target = "blockedUrlId", ignore = true)
    @Mapping(target = "blockedAt", ignore = true)
    BlockedUrl toEntity(CreateBlockedUrlRequest request);
}
