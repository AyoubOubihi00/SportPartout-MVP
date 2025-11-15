package fr.monclub.club_service.config.RabbitMQ.Publisher;

import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.config.RabbitMQ.Events.ClubDisabledEvent;
import fr.monclub.club_service.config.RabbitMQ.Events.ClubUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ClubDisabledPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ClubDisabledPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void publishClubDisabled(UUID clubId, OffsetDateTime date, boolean active) {
        System.out.println("here");
        ClubDisabledEvent event = new ClubDisabledEvent(
                clubId,
                date,
                active
        );

        rabbitTemplate.convertAndSend(
                "monclub.events",
                "club.disabled",
                event
        );

        System.out.println("ClubDisabledEvent envoyé pour le club " + clubId);
    }
}
