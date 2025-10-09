package fr.monclub.auth_service.auth.controller;

import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.mapper.RegisterInputToUserMapper;
import fr.monclub.auth_service.auth.model.*;
import fr.monclub.auth_service.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class AuthController {

    private final RegisterInputToUserMapper registerInputToUserMapper;
    private final AuthService authService;

    @MutationMapping
    public RegisterResponse register(@Argument RegisterInput input) {
        String message = authService.register(registerInputToUserMapper.apply(input));
        return new RegisterResponse(message);
    }

    @MutationMapping
    public AuthPayload login(@Argument LoginInput input) {
        String token = authService.login(input.getEmail(), input.getPassword());
        return new AuthPayload(token);
    }
}
