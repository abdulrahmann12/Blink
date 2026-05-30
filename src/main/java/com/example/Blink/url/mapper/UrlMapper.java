package com.example.Blink.url.mapper;

import com.example.Blink.url.dto.CreateUrlRequest;
import com.example.Blink.url.dto.UrlResponse;
import com.example.Blink.url.entity.Url;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UrlMapper {

    @Mapping(target = "urlId", ignore = true)
    @Mapping(target = "shortUrl", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "clickCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletesAt", ignore = true)
    Url toEntity(CreateUrlRequest createUrlRequest);

    UrlResponse toResponse(Url url);
}
