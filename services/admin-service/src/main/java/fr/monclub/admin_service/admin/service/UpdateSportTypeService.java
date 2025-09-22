package fr.monclub.admin_service.admin.service;

import fr.monclub.admin_service.admin.catalog.SportTypeCatalog;
import fr.monclub.admin_service.admin.model.SportType;
import fr.monclub.admin_service.admin.validators.UpdateSportTypeValidator;
import fr.monclub.admin_service.config.RabbitMQ.Events.SportTypeEvent;
import fr.monclub.admin_service.config.RabbitMQ.RabbitConfig;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UpdateSportTypeService {

    private final SportTypeCatalog sportTypeCatalog;
    private final UpdateSportTypeValidator updateSportTypeValidator;
    private final RabbitTemplate rabbitTemplate;

    public SportType apply(UUID id, String name) {
        SportType existing = sportTypeCatalog.getSportTypeById(id);
        updateSportTypeValidator.apply(name);
        existing.setName(name);

        SportTypeEvent event = new SportTypeEvent(id, name);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "sporttype.updated", event);

        return sportTypeCatalog.save(existing);
    }

}
