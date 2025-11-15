package fr.monclub.findclub_service.RabbitMQ.listener.Listener;

import fr.monclub.findclub_service.RabbitMQ.listener.ClubUpdatedEvent;
import fr.monclub.findclub_service.club.catalog.FindClubCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ClubEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ClubEventListener.class);
    private final FindClubCatalog findClubCatalog;

    public ClubEventListener(FindClubCatalog findClubCatalog) {
        this.findClubCatalog = findClubCatalog;
    }

    @RabbitListener(queues = "#{clubUpdatedQueueFindClubService.name}")
    public void onClubUpdated(ClubUpdatedEvent event) {
        logger.info("Received club updated event: {}", event.getClubId());
        try {
            findClubCatalog.updateClubInfo(
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
            logger.info("Club with id {} successfully updated in findClub-service.", event.getClubId());
        } catch (Exception e) {
            logger.error("Error while updating club with id {} in findClub-service.", event.getClubId(), e);
        }
    }
}
