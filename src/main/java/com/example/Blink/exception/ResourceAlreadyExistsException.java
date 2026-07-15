package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException() {
        super(Messages.RESOURCE_ALREADY_EXISTS);
    }
}

