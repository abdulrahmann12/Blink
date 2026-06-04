package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class InvalidNewPasswordException extends RuntimeException {
    public InvalidNewPasswordException() {
        super(Messages.INVALID_NEW_PASSWORD);
    }
}

