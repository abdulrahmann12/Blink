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
public class UserRegisteredConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitConstants.USER_REGISTERED_QUEUE)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            emailService.sendVerificationMail(event, Messages.VERIFY_EMAIL);
        } catch (Exception e) {
            throw new MailSendingException(e);
        }
    }

}
