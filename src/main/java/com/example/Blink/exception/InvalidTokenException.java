package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super(Messages.INVALID_TOKEN);
    }
}

