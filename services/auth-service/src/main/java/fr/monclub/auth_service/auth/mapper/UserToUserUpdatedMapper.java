package fr.monclub.auth_service.auth.mapper;

import fr.monclub.auth_service.auth.model.User;
import fr.monclub.auth_service.config.RabbitMQ.Events.UserUpdated;
import org.mapstruct.Mapper;

import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface UserToUserUpdatedMapper extends Function<User, UserUpdated>  {

    @Override
    UserUpdated apply(User user);
}
