package com.example.Blink.url_click.mapper;

import com.example.Blink.url_click.dto.ResourceClickResponse;
import com.example.Blink.url_click.entity.ResourceClick;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceClickMapper {

    ResourceClickResponse toResourceClickResponse(ResourceClick resourceClick);
}
