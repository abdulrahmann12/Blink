package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
        super(Messages.INVALID_VERIFICATION_CODE);
    }
}