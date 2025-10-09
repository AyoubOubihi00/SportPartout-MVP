package fr.monclub.auth_service.auth.mapper;

import fr.monclub.auth_service.auth.model.RegisterInput;
import fr.monclub.auth_service.auth.model.User;
import org.mapstruct.Mapper;

import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface RegisterInputToUserMapper extends Function<RegisterInput, User>  {

    @Override
    User apply(RegisterInput registerInput);
}
