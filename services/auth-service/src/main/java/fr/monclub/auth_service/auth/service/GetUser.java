package fr.monclub.auth_service.auth.service;


import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.model.User;
import fr.monclub.auth_service.shared.entity.GetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetUser implements GetEntity<User> {

    private final UserCatalog userCatalog;

    @Override
    public User apply() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userCatalog.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}