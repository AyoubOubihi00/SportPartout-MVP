package fr.monclub.club_membership_service.RabbitMQ.listener;




import fr.monclub.club_membership_service.RabbitMQ.ClubUpdatedEvent;
import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.repository.ClubMemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ClubEventListener {

    private final ClubMemberCatalog clubMemberCatalog;

    @RabbitListener(queues = "#{clubUpdatedQueue.name}")
    public void handleClubUpdatedEvent(ClubUpdatedEvent event) {
        System.out.println("ClubUpdatedEvent reçu : " + event.getClubId());
        clubMemberCatalog.updateClubInfo(
                event.getClubId(),
                event.getName(),
                event.getTypeId(),
                event.getTypeName(),
                event.getCity(),
                event.getAddress(),
                event.getCountry(),
                event.getPostalCode(),
                event.getPhoneNumber(),
                event.getEmail(),
                event.getLatitude(),
                event.getLongitude(),
                event.getUpdatedAt()
        );
    }
}