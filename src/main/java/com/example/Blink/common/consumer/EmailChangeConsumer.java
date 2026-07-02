package com.example.Blink.common.consumer;

import com.example.Blink.common.events.EmailChangeEvent;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.service.EmailService;
import com.example.Blink.config.rabbitconfig.RabbitConstants;
import com.example.Blink.exception.MailSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailChangeConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitConstants.USER_EMAIL_CHANGE_QUEUE)
    public void handleEmailChangeEvent(EmailChangeEvent event) {
        try {
            emailService.sendEmailChangeMail(event, Messages.MAIL_CHANGE);
        } catch (Exception e) {
            throw new MailSendingException(e);
        }
    }
}
