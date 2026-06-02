package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class AlreadyLoggedOutException extends RuntimeException {
    public AlreadyLoggedOutException() {
        super(Messages.ALREADY_LOGGED_OUT);
    }
}

