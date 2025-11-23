package fr.monclub.findclub_service.RabbitMQ.listener.Listener;

import fr.monclub.findclub_service.RabbitMQ.listener.ClubReactivedEvent;
import fr.monclub.findclub_service.club.catalog.FindClubCatalog;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ClubReactivedEventListener {
    private final FindClubCatalog findClubCatalog;

    @RabbitListener(queues = "#{clubReactivedQueueFindClubService.name}")
    public void handleClubDisabledEvent(ClubReactivedEvent event) {
        System.out.println("ClubReactived reçu : " + event.getClubId());
        findClubCatalog.reactiveClub(event);
    }
}
