package fr.monclub.auth_service.auth.controller;

import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.model.RegisterResponse;
import fr.monclub.auth_service.auth.model.UpdateUserInput;
import fr.monclub.auth_service.auth.model.UpdateUserResponse;
import fr.monclub.auth_service.auth.model.User;
import fr.monclub.auth_service.auth.service.GetUser;
import fr.monclub.auth_service.auth.service.UpdateService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UserController {

    private final GetUser getUser;
    private final UpdateService updateUser;

    @QueryMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public User me() {
        return getUser.apply();
    }

    @MutationMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public UpdateUserResponse updateUser(@Argument UpdateUserInput input) {
        String message = updateUser.update(getUser.apply(),input);
        return new UpdateUserResponse(message);

    }
}
