package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;
import jakarta.mail.Message;

public class AliasAlreadyUsed extends RuntimeException {
    public AliasAlreadyUsed() {
        super(Messages.ALIAS_ALREADY_USED);
    }
}
