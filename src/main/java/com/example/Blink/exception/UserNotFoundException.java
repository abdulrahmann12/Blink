package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super(Messages.USER_NOT_FOUND);
    }
}

