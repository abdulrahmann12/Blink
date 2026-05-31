package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UserNotActiveException extends RuntimeException {

    public UserNotActiveException() {
        super(Messages.USER_NOT_ACTIVE);
    }
}

