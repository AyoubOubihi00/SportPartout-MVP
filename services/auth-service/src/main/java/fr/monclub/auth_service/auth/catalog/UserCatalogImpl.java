package fr.monclub.auth_service.auth.catalog;


import fr.monclub.auth_service.auth.model.User;
import fr.monclub.auth_service.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCatalogImpl implements UserCatalog {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return  userRepository.findById(id);
    }
}
