package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class VerificationCodeAlreadySentException extends RuntimeException {
    public VerificationCodeAlreadySentException() {
        super(Messages.VERIFICATION_CODE_ALREADY_SENT);
    }
}
