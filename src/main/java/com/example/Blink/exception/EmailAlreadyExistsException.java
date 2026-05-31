package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super(Messages.EMAIL_ALREADY_EXISTS);
    }
}

