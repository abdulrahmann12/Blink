package com.example.Blink.resource.mapper;

import com.example.Blink.resource.dto.CreateResourceRequest;
import com.example.Blink.resource.dto.ResourceResponse;
import com.example.Blink.resource.entity.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(target = "resourceId", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "passwordProtected", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "clickCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Resource toEntity(CreateResourceRequest createResourceRequest);

    @Mapping(target = "userName", source = "user.username") 
    ResourceResponse toResponse(Resource resource);
}