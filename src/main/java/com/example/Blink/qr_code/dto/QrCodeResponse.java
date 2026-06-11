package com.example.Blink.qr_code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeResponse {
    private UUID qrId;
    private String imagePath;
    private String qrText;
    private LocalDateTime createdAt;
}