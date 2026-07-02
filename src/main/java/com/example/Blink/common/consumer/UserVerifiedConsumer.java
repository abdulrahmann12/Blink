package com.example.Blink.common.consumer;

import com.example.Blink.common.events.UserRegisteredEvent;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.service.EmailService;
import com.example.Blink.config.rabbitconfig.RabbitConstants;
import com.example.Blink.exception.MailSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserVerifiedConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitConstants.USER_EMAIL_VERIFIED_QUEUE)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            emailService.sendWelcomeMail(event, Messages.WELCOME_MAIL);
        } catch (Exception e) {
            throw new MailSendingException();
        }
    }

}
