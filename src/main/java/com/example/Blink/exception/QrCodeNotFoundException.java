package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class QrCodeNotFoundException extends RuntimeException{

    public QrCodeNotFoundException(){
        super(Messages.QR_NOT_FOUND);
    }
}
