package fr.monclub.club_membership_service.RabbitMQ.listener;

import fr.monclub.club_membership_service.RabbitMQ.ClubDisabledEvent;
import fr.monclub.club_membership_service.RabbitMQ.ClubUpdatedEvent;
import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ClubDisabledEventListener {

    private final ClubMemberCatalog clubMemberCatalog;

    @RabbitListener(queues = "#{clubDisabledQueue.name}")
    public void handleClubDisabledEvent(ClubDisabledEvent event) {
        System.out.println("ClubDisabledEvent reçu : " + event.getClubId());
        boolean updated = clubMemberCatalog.disableClub(event);

        if (!updated) {
            System.out.println("[WARN] Aucun membre trouvé pour le clubId : " + event.getClubId());
        } else {
            System.out.println("[INFO] Tous les membres du club ont été désactivés.");
        }
    }
}