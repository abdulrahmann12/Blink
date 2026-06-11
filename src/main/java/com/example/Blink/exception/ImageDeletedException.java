package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class ImageDeletedException extends RuntimeException {

    public ImageDeletedException() {
        super(Messages.IMAGE_DELETED_FAILED);
    }
}

