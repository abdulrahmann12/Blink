package com.example.Blink.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {

    private String message;
    private Object data;
    private Date timestamp = new Date();

    private BaseResponse(String message){
        this.message = message;
    }
    public BaseResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }
}
