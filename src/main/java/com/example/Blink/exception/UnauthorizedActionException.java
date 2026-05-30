package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UnauthorizedActionException extends RuntimeException{

    public UnauthorizedActionException(String message) {
        super(message);
    }
}