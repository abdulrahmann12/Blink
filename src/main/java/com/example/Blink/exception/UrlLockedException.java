package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UrlLockedException extends RuntimeException {

    public UrlLockedException() {
        super(Messages.URL_LOCKED);
    }
}

