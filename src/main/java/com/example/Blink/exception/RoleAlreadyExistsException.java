package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class RoleAlreadyExistsException extends RuntimeException {

    public RoleAlreadyExistsException() {
        super(Messages.ROLE_ALREADY_EXISTS);
    }
}

