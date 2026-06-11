package com.example.Blink.qr_code.mapper;

import com.example.Blink.qr_code.dto.QrCodeResponse;
import com.example.Blink.qr_code.entity.QrCode;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QrCodeMapper {

    QrCodeResponse toResponse(QrCode qrCode);
}
