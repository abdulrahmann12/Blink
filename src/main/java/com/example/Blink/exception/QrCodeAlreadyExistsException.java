package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class QrCodeAlreadyExistsException extends RuntimeException {

    public QrCodeAlreadyExistsException() {
        super(Messages.QR_ALREADY_EXISTS);
    }
}

