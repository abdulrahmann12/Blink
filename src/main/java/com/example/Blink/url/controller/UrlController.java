package com.example.Blink.url.controller;

import com.example.Blink.common.dto.BaseResponse;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.url.dto.CreateUrlRequest;
import com.example.Blink.url.service.UrlService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<BaseResponse> generateShortUrl(@Valid @RequestBody CreateUrlRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(Messages.URL_CREATED, urlService.generateShortUrl(request)));
    }
}
