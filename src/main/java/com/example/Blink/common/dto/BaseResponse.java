package com.example.Blink.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {

    private String message;
    private Object data;
    private Instant timestamp = Instant.now();

    public BaseResponse(String message){
        this.message = message;
    }

    public BaseResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }
}
