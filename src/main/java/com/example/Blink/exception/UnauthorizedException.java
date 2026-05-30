package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException() {
        super(Messages.UNAUTHORIZED);
    }
}