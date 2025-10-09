package fr.monclub.auth_service.auth.service;

import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.model.User;
import fr.monclub.auth_service.config.JWT.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserCatalog userCatalog;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public String register(User user) {
        System.out.println("Registering user: " + user.getEmail());
        if (userCatalog.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userCatalog.save(user);
        System.out.println("Registered user: " + user.getEmail());
        return "User registered successfully";
    }

    public String login(String username, String password) {
        User user = userCatalog.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return jwtUtils.generateToken(user);
    }
}