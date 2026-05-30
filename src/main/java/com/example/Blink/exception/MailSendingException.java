package com.example.Blink.exception;

import com.example.Blink.common.messages.Messages;

public class MailSendingException extends RuntimeException {

    public MailSendingException() {
        super(Messages.FAILED_EMAIL);
    }

}