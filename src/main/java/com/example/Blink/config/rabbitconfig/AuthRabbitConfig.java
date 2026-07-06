package com.example.Blink.config.rabbitconfig;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthRabbitConfig {

    // ── Main Exchange ──────────────────────────────────────────────────────

    @Bean
    public TopicExchange authExchange(){
        return new TopicExchange(RabbitConstants.AUTH_EXCHANGE);
    }

    // ── Dead-Letter Exchange ───────────────────────────────────────────────

    @Bean
    public DirectExchange authDlxExchange() {
        return new DirectExchange(RabbitConstants.AUTH_DLX_EXCHANGE);
    }

    // ── Helper: build a queue with DLX arguments ──────────────────────────

    private Queue buildQueueWithDlx(String queueName) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", RabbitConstants.AUTH_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", queueName)
                .build();
    }

    // ── Main Queues (all configured with DLX) ─────────────────────────────

    @Bean
    public Queue userRegisteredQueue(){
        return buildQueueWithDlx(RabbitConstants.USER_REGISTERED_QUEUE);
    }

    @Bean
    public Queue userVerifiedQueue(){
        return buildQueueWithDlx(RabbitConstants.USER_EMAIL_VERIFIED_QUEUE);
    }

    @Bean
    public Queue userChangeEmailQueue(){
        return buildQueueWithDlx(RabbitConstants.USER_EMAIL_CHANGE_QUEUE);
    }

    @Bean
    public Queue passwordResetQueue(){
        return buildQueueWithDlx(RabbitConstants.PASSWORD_RESET_QUEUE);
    }

    @Bean
    public Queue codeRegeneratedQueue(){
        return buildQueueWithDlx(RabbitConstants.CODE_REGENERATED_QUEUE);
    }

    // ── Dead-Letter Queues ────────────────────────────────────────────────

    @Bean
    public Queue userRegisteredDlq() {
        return QueueBuilder.durable(RabbitConstants.USER_REGISTERED_DLQ).build();
    }

    @Bean
    public Queue userVerifiedDlq() {
        return QueueBuilder.durable(RabbitConstants.USER_EMAIL_VERIFIED_DLQ).build();
    }

    @Bean
    public Queue userChangeEmailDlq() {
        return QueueBuilder.durable(RabbitConstants.USER_EMAIL_CHANGE_DLQ).build();
    }

    @Bean
    public Queue passwordResetDlq() {
        return QueueBuilder.durable(RabbitConstants.PASSWORD_RESET_DLQ).build();
    }

    @Bean
    public Queue codeRegeneratedDlq() {
        return QueueBuilder.durable(RabbitConstants.CODE_REGENERATED_DLQ).build();
    }

    // ── Main Queue Bindings ───────────────────────────────────────────────

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

    // ── Dead-Letter Queue Bindings ────────────────────────────────────────

    @Bean
    public Binding userRegisteredDlqBinding(DirectExchange authDlxExchange, Queue userRegisteredDlq) {
        return BindingBuilder.bind(userRegisteredDlq).to(authDlxExchange).with(RabbitConstants.USER_REGISTERED_QUEUE);
    }

    @Bean
    public Binding userVerifiedDlqBinding(DirectExchange authDlxExchange, Queue userVerifiedDlq) {
        return BindingBuilder.bind(userVerifiedDlq).to(authDlxExchange).with(RabbitConstants.USER_EMAIL_VERIFIED_QUEUE);
    }

    @Bean
    public Binding userChangeEmailDlqBinding(DirectExchange authDlxExchange, Queue userChangeEmailDlq) {
        return BindingBuilder.bind(userChangeEmailDlq).to(authDlxExchange).with(RabbitConstants.USER_EMAIL_CHANGE_QUEUE);
    }

    @Bean
    public Binding passwordResetDlqBinding(DirectExchange authDlxExchange, Queue passwordResetDlq) {
        return BindingBuilder.bind(passwordResetDlq).to(authDlxExchange).with(RabbitConstants.PASSWORD_RESET_QUEUE);
    }

    @Bean
    public Binding codeRegeneratedDlqBinding(DirectExchange authDlxExchange, Queue codeRegeneratedDlq) {
        return BindingBuilder.bind(codeRegeneratedDlq).to(authDlxExchange).with(RabbitConstants.CODE_REGENERATED_QUEUE);
    }
}
