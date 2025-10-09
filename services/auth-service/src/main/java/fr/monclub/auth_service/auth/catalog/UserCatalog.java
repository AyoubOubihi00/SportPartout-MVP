package fr.monclub.auth_service.auth.catalog;


import fr.monclub.auth_service.auth.model.User;

import java.util.Optional;
import java.util.UUID;


public interface UserCatalog {

    Optional<User> findByEmail(String username);

    User save(User user);

    Optional<User> findById(UUID id);
}
