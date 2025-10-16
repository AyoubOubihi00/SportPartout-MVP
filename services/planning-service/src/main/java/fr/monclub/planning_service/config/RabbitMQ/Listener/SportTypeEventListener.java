package fr.monclub.planning_service.config.RabbitMQ.Listener;

import fr.monclub.planning_service.config.RabbitMQ.Events.SportTypeEvent;
import fr.monclub.planning_service.planning.service.UpdateSportTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
@RequiredArgsConstructor
public class SportTypeEventListener {

    private final UpdateSportTypeService updateSportTypeService;

    @RabbitListener(queues = "#{sportTypeQueuePlanningService.name}")
    public void handleSportTypeEvent(SportTypeEvent event) {
        updateSportTypeService.apply(event.getUuid(), event.getName());
    }
}
