package com.example.Blink.url_click.mapper;

import com.example.Blink.url_click.dto.UrlClickResponse;
import com.example.Blink.url_click.entity.UrlClick;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrlClickMapper {

    UrlClickResponse toUrlClickResponse(UrlClick urlClick);
}
