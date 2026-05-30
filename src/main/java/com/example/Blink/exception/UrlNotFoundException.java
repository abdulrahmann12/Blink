package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class UrlNotFoundException extends RuntimeException{

    public UrlNotFoundException(){
        super(Messages.URL_NOT_FOUND);
    }
}
