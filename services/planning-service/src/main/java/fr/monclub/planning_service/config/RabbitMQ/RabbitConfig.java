package fr.monclub.planning_service.config.RabbitMQ;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "monclub.events";
    public static final String SPORT_TYPE_ROUTING_KEY = "sporttype.updated";
    public static final String CLUB_UPDATED_ROUTING_KEY = "club.updated";

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

    @Bean
    public Queue sportTypeQueuePlanningService() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding sportTypeBindingPlanningService(TopicExchange appEventsExchange,
                                                   Queue sportTypeQueuePlanningService) {
        return BindingBuilder
                .bind(sportTypeQueuePlanningService)
                .to(appEventsExchange)
                .with(SPORT_TYPE_ROUTING_KEY);
    }

    @Bean
    public Queue clubUpdatedQueuePlanningService() {
        return new Queue("club.updated.planning", true);
    }

    @Bean
    public Binding clubUpdatedBindingPlanningService(TopicExchange appEventsExchange,Queue clubUpdatedQueuePlanningService) {

        return BindingBuilder
                .bind(clubUpdatedQueuePlanningService)
                .to(appEventsExchange)
                .with(CLUB_UPDATED_ROUTING_KEY);

    }
}
