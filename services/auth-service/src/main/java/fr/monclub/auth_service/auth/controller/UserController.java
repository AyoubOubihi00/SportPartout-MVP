package fr.monclub.auth_service.auth.controller;

import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.model.User;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserCatalog userCatalog;

    @QueryMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public User me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userCatalog.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
