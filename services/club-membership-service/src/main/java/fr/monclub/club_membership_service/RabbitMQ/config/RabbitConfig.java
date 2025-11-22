package fr.monclub.club_membership_service.RabbitMQ.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "monclub.events";

    @Bean
    public TopicExchange appEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }

    // Queue pour écouter les mises à jour des clubs
    @Bean
    public Queue clubUpdatedQueue() {
        return new AnonymousQueue(); // ou une queue nommée si tu veux la persister
    }

    // Binding entre la queue et l'exchange
    @Bean
    public Binding clubUpdatedBinding(TopicExchange appEventsExchange, Queue clubUpdatedQueue) {
        return BindingBuilder
                .bind(clubUpdatedQueue)
                .to(appEventsExchange)
                .with("club.updated");
    }

    @Bean
    public Queue clubDisabledQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding clubDisabledBinding(TopicExchange appEventsExchange, Queue clubDisabledQueue) {
        return BindingBuilder
                .bind(clubDisabledQueue)
                .to(appEventsExchange)
                .with("club.disabled");
    }

    @Bean
    public Queue clubReactived() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding clubReactivedBinding(TopicExchange appEventsExchange, Queue clubReactived) {
        return BindingBuilder
                .bind(clubReactived)
                .to(appEventsExchange)
                .with("club.reactived");
    }

    @Bean
    public Queue userUpdatedQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding userUpdatedBinding(TopicExchange appEventsExchange, Queue userUpdatedQueue) {
        return BindingBuilder
                .bind(userUpdatedQueue)
                .to(appEventsExchange)
                .with("user.updated");
    }

}

