package fr.monclub.auth_service.auth.service;

import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.mapper.UserToUserUpdatedMapper;
import fr.monclub.auth_service.auth.model.UpdateUserInput;
import fr.monclub.auth_service.auth.model.User;
import fr.monclub.auth_service.auth.repository.UserRepository;
import fr.monclub.auth_service.config.RabbitMQ.Events.UserUpdated;
import fr.monclub.auth_service.config.RabbitMQ.RabbitConfig;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UpdateService {

    private final UserToUserUpdatedMapper userToUserUpdatedMapper;
    private final UserCatalog userCatalog;
    private final RabbitTemplate rabbitTemplate;

    public String update(User user, UpdateUserInput input) {
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setBirthDate(input.getBirthDate());
        user.setPhone(input.getPhone());
        userCatalog.save(user);

        UserUpdated event = userToUserUpdatedMapper.apply(user);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "user.updated", event);

        return "User updated successfully";
    }
}
