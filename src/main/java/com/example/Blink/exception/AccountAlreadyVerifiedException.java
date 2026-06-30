package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class AccountAlreadyVerifiedException extends RuntimeException {

    public AccountAlreadyVerifiedException() {
        super(Messages.ACCOUNT_ALREADY_VERIFIED);
    }
}

