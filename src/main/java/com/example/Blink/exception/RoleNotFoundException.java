package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException() {
        super(Messages.ROLE_NOT_FOUND);
    }
}

