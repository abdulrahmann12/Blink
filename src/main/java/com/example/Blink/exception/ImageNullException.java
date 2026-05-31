package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class ImageNullException extends RuntimeException {
    public ImageNullException() {
        super(Messages.IMAGE_NULL);
    }
}

