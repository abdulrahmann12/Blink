package com.example.Blink.common.service;

import com.example.Blink.exception.ImageDeletedException;
import com.example.Blink.exception.ImageUploadException;

import java.io.IOException;
import java.util.Map;

import com.example.Blink.common.dto.ImageUploadResult;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;

    public ImageUploadResult uploadImage(byte[] imageBytes) {
        try {
            Map<?, ?> result = cloudinary.uploader()
                    .upload(imageBytes, ObjectUtils.emptyMap());
            return new ImageUploadResult(
                    result.get("secure_url").toString(),
                    result.get("public_id").toString()
            );
        } catch (IOException e) {
            throw new ImageUploadException();
        }
    }
    public void deleteImage(String publicId) {
        try {

            cloudinary.uploader()
                    .destroy(publicId, ObjectUtils.emptyMap());

        } catch (IOException e) {
            throw new ImageDeletedException();
        }
    }

}