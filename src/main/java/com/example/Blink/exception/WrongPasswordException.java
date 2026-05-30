package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException() {
        super(Messages.WRONG_PASSWORD);
    }
}

