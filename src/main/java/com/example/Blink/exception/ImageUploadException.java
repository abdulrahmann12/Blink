package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class ImageUploadException extends RuntimeException {

    public ImageUploadException() {
        super(Messages.IMAGE_UPLOAD_FAILED);
    }
}

