package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(){
        super(Messages.RESOURCE_NOT_FOUND);
    }
}
