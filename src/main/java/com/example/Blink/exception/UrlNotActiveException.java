package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UrlNotActiveException extends RuntimeException {

    public UrlNotActiveException() {
        super(Messages.URL_INACTIVE);
    }
}

