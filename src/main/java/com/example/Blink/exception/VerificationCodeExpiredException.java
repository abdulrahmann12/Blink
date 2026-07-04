package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class VerificationCodeExpiredException extends RuntimeException{
    public VerificationCodeExpiredException() {
        super(Messages.VERIFICATION_CODE_EXPIRED);
    }
}
