package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class InvalidUrlException extends RuntimeException {

    public InvalidUrlException() {
        super(Messages.INVALID_URL);
    }
}

