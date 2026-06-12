package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class RateLimitExceededException extends RuntimeException{
    public RateLimitExceededException(){
        super(Messages.TOO_MANY_REQUESTS);
    }
}
