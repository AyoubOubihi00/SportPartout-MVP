package fr.monclub.club_service.config.RabbitMQ.Publisher;

import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.config.RabbitMQ.Events.ClubUpdatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClubEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ClubEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * @param clubId ID du club à mettre à jour
     * @param club   données de mise à jour (UpdateClubInput)
     */
    public void publishClubUpdated(UUID clubId, UpdateClubInput club) {
        System.out.println("here");
        ClubUpdatedEvent event = new ClubUpdatedEvent(
                clubId,
                club.getName(),
                club.getTypeId(),
                club.getTypeName(),
                club.getCity(),
                club.getAddress(),
                club.getCountry(),
                club.getPostalCode(),
                club.getPhoneNumber(),
                club.getEmail(),
                club.getLatitude(),
                club.getLongitude(),
                club.getUpdatedAt()
        );

        rabbitTemplate.convertAndSend(
                "monclub.events",
                "club.updated",
                event
        );

        System.out.println("ClubUpdatedEvent envoyé pour le club " + clubId);
    }
}
