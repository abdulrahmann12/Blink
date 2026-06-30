package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UserNotVerifyException extends RuntimeException {
    public UserNotVerifyException() {
        super(Messages.ACCOUNT_NOT_VERIFIED);
    }
}