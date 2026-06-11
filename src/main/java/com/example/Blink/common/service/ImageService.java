package com.example.Blink.common.service;

import com.example.Blink.exception.ImageDeletedException;
import com.example.Blink.exception.ImageUploadException;

import java.io.IOException;
import java.util.Map;

import com.example.Blink.common.dto.ImageUploadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class ImageService {

    private Cloudinary cloudinary;

    @Autowired
    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

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