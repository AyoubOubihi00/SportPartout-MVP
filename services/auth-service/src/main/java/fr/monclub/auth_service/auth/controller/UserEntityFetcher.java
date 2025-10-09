package fr.monclub.auth_service.auth.controller;

import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.model.User;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class UserEntityFetcher {
    private final UserCatalog userCatalog;

    @EntityMapping(name = "User")
    public User resolveUser(java.util.Map<String, Object> rep) {
        Object id = rep.get("id");
        return id == null ? null : userCatalog.findById((java.util.UUID) id).orElse(null);
    }
}