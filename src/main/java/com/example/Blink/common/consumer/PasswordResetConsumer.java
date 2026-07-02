package com.example.Blink.common.consumer;

import com.example.Blink.common.events.PasswordResetRequestedEvent;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.service.EmailService;
import com.example.Blink.config.rabbitconfig.RabbitConstants;
import com.example.Blink.exception.MailSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitConstants.PASSWORD_RESET_QUEUE)
    public void handlePasswordResetEvent(PasswordResetRequestedEvent event) {
        try {
            emailService.sendPasswordResetMail(event, Messages.RESET_PASSWORD);
        } catch (Exception e) {
            throw new MailSendingException(e);
        }
    }
}
