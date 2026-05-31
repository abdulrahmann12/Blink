package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UserAlreadyDeactivatedException extends RuntimeException {
    public UserAlreadyDeactivatedException() {
        super(Messages.USER_ALREADY_DEACTIVATED);
    }
}

