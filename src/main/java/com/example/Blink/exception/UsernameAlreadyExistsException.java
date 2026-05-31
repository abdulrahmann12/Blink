package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException() {
        super(Messages.USERNAME_ALREADY_EXISTS);
    }
}

