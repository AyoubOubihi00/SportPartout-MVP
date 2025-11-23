package fr.monclub.club_service.config.RabbitMQ.Publisher;

import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.config.RabbitMQ.Events.ClubDisabledEvent;
import fr.monclub.club_service.config.RabbitMQ.Events.ClubReactivedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ClubReactivedEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ClubReactivedEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void publishReactivated(UUID clubId, boolean active) {
        ClubReactivedEvent event = new ClubReactivedEvent(
                clubId,
                active
        );

        rabbitTemplate.convertAndSend(
                "monclub.events",
                "club.reactived",
                event
        );

        System.out.println("ClubReactived envoyé pour le club " + clubId);
    }
}
