package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UserAlreadyActivatedException extends RuntimeException {
    public UserAlreadyActivatedException() {
        super(Messages.USER_ALREADY_ACTIVATED);
    }
}

