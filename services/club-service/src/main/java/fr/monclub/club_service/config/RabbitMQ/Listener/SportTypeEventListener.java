package fr.monclub.club_service.config.RabbitMQ.Listener;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.service.UpdateSportTypeService;
import fr.monclub.club_service.config.RabbitMQ.Events.SportTypeEvent;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SportTypeEventListener {

    private final UpdateSportTypeService updateSportTypeService;

    @RabbitListener(queues = "#{sportTypeQueueClubService.name}")
    public void handleSportTypeEvent(SportTypeEvent event) {
            System.out.println("Le type de sport ayant pour id " + event.getUuid() + " a été mis à jour avec le nom : " + event.getName());
            updateSportTypeService.apply(event.getUuid(), event.getName());
    }
}
