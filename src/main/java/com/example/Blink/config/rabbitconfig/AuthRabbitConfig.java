package com.example.Blink.config.rabbitconfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthRabbitConfig {

    @Bean
    public TopicExchange authExchange(){
        return new TopicExchange(RabbitConstants.AUTH_EXCHANGE);
    }

    @Bean
    public Queue userRegisteredQueue(){
        return new Queue(RabbitConstants.USER_REGISTERED_QUEUE);
    }

    @Bean
    public Queue userVerifiedQueue(){
        return new Queue(RabbitConstants.USER_EMAIL_VERIFIED_QUEUE);
    }

    @Bean
    public Queue userChangeEmailQueue(){
        return new Queue(RabbitConstants.USER_EMAIL_CHANGE_QUEUE);
    }

    @Bean
    public Queue passwordResetQueue(){
        return new Queue(RabbitConstants.PASSWORD_RESET_QUEUE);
    }

    @Bean
    public Queue codeRegeneratedQueue(){
        return new Queue(RabbitConstants.CODE_REGENERATED_QUEUE);
    }

    @Bean
    public Binding userRegisteredBinding(TopicExchange authExchange, Queue userRegisteredQueue){
        return BindingBuilder.bind(userRegisteredQueue).to(authExchange).with(RabbitConstants.USER_REGISTERED_KEY);
    }

    @Bean
    public Binding userChangeEmailBinding(TopicExchange authExchange, Queue userChangeEmailQueue){
        return BindingBuilder.bind(userChangeEmailQueue).to(authExchange).with(RabbitConstants.USER_EMAIL_CHANGE_KEY);
    }

    @Bean
    public Binding passwordResetBinding(TopicExchange authExchange, Queue passwordResetQueue) {
        return BindingBuilder.bind(passwordResetQueue).to(authExchange).with(RabbitConstants.PASSWORD_RESET_KEY);
    }

    @Bean
    public Binding regenerateCodeBinding(TopicExchange authExchange, Queue codeRegeneratedQueue) {
        return BindingBuilder.bind(codeRegeneratedQueue).to(authExchange).with(RabbitConstants.CODE_REGENERATED_KEY);
    }

    @Bean
    public Binding userVerifiedBinding(TopicExchange authExchange, Queue userVerifiedQueue) {
        return BindingBuilder.bind(userVerifiedQueue).to(authExchange).with(RabbitConstants.USER_EMAIL_VERIFIED_KEY);
    }
}
