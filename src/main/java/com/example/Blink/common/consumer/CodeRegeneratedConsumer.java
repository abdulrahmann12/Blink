package com.example.Blink.common.consumer;

import com.example.Blink.common.events.CodeRegeneratedEvent;
import com.example.Blink.common.messages.Messages;
import com.example.Blink.common.service.EmailService;
import com.example.Blink.config.rabbitconfig.RabbitConstants;
import com.example.Blink.exception.MailSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CodeRegeneratedConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = RabbitConstants.CODE_REGENERATED_QUEUE)
    public void handleCodeRegeneratedEvent(CodeRegeneratedEvent event) {
        try{
            emailService.sendRegenerateCode(event, Messages.RESEND_CODE);
        }
        catch (Exception e){
            throw new MailSendingException(e);
        }

    }
}
