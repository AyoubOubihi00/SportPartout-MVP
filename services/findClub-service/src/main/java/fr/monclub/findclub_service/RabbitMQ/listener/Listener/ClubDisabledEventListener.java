package fr.monclub.findclub_service.RabbitMQ.listener.Listener;


import fr.monclub.findclub_service.RabbitMQ.listener.ClubDisabledEvent;
import fr.monclub.findclub_service.club.catalog.FindClubCatalog;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ClubDisabledEventListener {

    private final FindClubCatalog findClubCatalog;

    @RabbitListener(queues = "#{clubDisabledQueueFindClubService.name}")
    public void handleClubDisabledEvent(ClubDisabledEvent event) {
        System.out.println("ClubDisabledEvent reçu : " + event.getClubId());
        findClubCatalog.disableClub(event);
    }
}