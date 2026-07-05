package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class ShortCodeExhaustedException extends RuntimeException {
    public ShortCodeExhaustedException() {
        super(Messages.SHORT_CODE_EXHAUSTED);
    }
}
