package fr.monclub.planning_service.config.RabbitMQ.Listener;


import fr.monclub.planning_service.config.RabbitMQ.Events.ClubUpdatedEvent;
import fr.monclub.planning_service.planning.service.UpdateClubService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Profile("!test")
@Service
public class ClubEventListener {

    private final UpdateClubService updateClubService;

    @RabbitListener(queues = "#{clubUpdatedQueuePlanningService.name}")
    public void handleClubUpdatedEvent(ClubUpdatedEvent event) {
        updateClubService.apply(event);
    }
}