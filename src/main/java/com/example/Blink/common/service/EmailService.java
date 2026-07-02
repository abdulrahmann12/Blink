package com.example.Blink.common.service;

import com.example.Blink.common.events.CodeRegeneratedEvent;
import com.example.Blink.common.events.EmailChangeEvent;
import com.example.Blink.common.events.PasswordResetRequestedEvent;
import com.example.Blink.common.events.UserRegisteredEvent;
import com.example.Blink.exception.MailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendWelcomeMail(UserRegisteredEvent userRegisteredEvent, String subject) {
        sendEmail(
                userRegisteredEvent.getEmail(),
                subject,
                "emails/welcome",
                new ContextBuilder()
                        .add("name", userRegisteredEvent.getFullName())
                        .build()
        );
    }

    public void sendVerificationMail(UserRegisteredEvent userRegisteredEvent, String subject) {
        sendEmail(userRegisteredEvent.getEmail(),
                subject,
                "emails/verify-email",
                new ContextBuilder()
                        .add("name", userRegisteredEvent.getFullName())
                        .add("code", userRegisteredEvent.getCode())
                        .build()
        );
    }

    public void sendPasswordResetMail(PasswordResetRequestedEvent passwordResetRequestedEvent,String subject) {
        sendEmail(passwordResetRequestedEvent.getEmail(),
                subject,
                "emails/password-reset",
                new ContextBuilder().add("name", passwordResetRequestedEvent.getUsername())
                        .add("code", passwordResetRequestedEvent.getCode())
                        .build()
        );
    }

    public void sendEmailChangeMail(EmailChangeEvent emailChangeEvent, String subject) {
        sendEmail(emailChangeEvent.getOldEmail(),
                subject,
                "emails/change-email",
                new ContextBuilder().add("name", emailChangeEvent.getUsername())
                        .add("code", emailChangeEvent.getCode())
                        .add("newMail", emailChangeEvent.getNewEmail())
                        .build()
        );
    }

    public void sendRegenerateCode(CodeRegeneratedEvent user, String subject) {
        sendEmail(user.getEmail(), subject, "emails/send-code", // template path inside /resources/templates
                new ContextBuilder().add("name", user.getUsername()).add("code", user.getCode()).build());
    }

    private void sendEmail(String to, String subject, String templatePath, Context context) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = templateEngine.process(templatePath, context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailSendingException(e);
        }
    }
    private static class ContextBuilder {
        private final Context context = new Context();

        public ContextBuilder add(String key, Object value) {
            context.setVariable(key, value);
            return this;
        }

        public Context build() {
            return context;
        }
    }
}
