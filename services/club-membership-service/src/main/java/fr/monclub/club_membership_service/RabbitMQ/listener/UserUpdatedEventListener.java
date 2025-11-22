package fr.monclub.club_membership_service.RabbitMQ.listener;

import fr.monclub.club_membership_service.RabbitMQ.ClubDisabledEvent;
import fr.monclub.club_membership_service.RabbitMQ.UserUpdatedEvent;
import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserUpdatedEventListener {

    private final ClubMemberCatalog clubMemberCatalog;

    @RabbitListener(queues = "#{userUpdatedQueue.name}")
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        boolean updated = clubMemberCatalog.updateUser(event);

        if (!updated) {
            System.out.println("[WARN] Aucun membre trouvé pour l'id : " + event.getUuid());
        } else {
            System.out.println("[INFO] Les informations de l'utilisateurs ont été mis à jours");
        }
    }
}