package fr.monclub.club_membership_service.RabbitMQ.listener;

import fr.monclub.club_membership_service.RabbitMQ.ClubDisabledEvent;
import fr.monclub.club_membership_service.RabbitMQ.ClubReactivedEvent;
import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ClubReactivedEventListener {
    private final ClubMemberCatalog clubMemberCatalog;

    @RabbitListener(queues = "#{clubDisabledQueue.name}")
    public void handleClubDisabledEvent(ClubReactivedEvent event) {
        System.out.println("ClubReactived reçu : " + event.getClubId());
        clubMemberCatalog.reactiveClub(event);
    }
}
