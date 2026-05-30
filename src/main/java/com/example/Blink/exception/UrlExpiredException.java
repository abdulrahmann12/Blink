package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UrlExpiredException extends RuntimeException {

    public UrlExpiredException() {
        super(Messages.URL_EXPIRED);
    }
}

