package fr.monclub.admin_service.fixtures;


import fr.monclub.admin_service.admin.model.SportType;
import fr.monclub.admin_service.admin.catalog.SportTypeCatalog;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("dev")
@AllArgsConstructor
public class TypeSportFixtures implements CommandLineRunner {

    private final SportTypeCatalog catalog;

    public static final UUID UUID_FOOTBALL = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    public static final UUID UUID_BASKETBALL = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    public static final UUID UUID_TENNIS = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
    public static final UUID UUID_RUGBY = UUID.fromString("123e4567-e89b-12d3-a456-426614174004");
    public static final UUID UUID_HAND_BALL = UUID.fromString("123e4567-e89b-12d3-a456-426614174005");
    public static final UUID UUID_VOLLEY_BALL = UUID.fromString("123e4567-e89b-12d3-a456-426614174006");
    public static final UUID UUID_BASE_BALL = UUID.fromString("123e4567-e89b-12d3-a456-426614174007");
    public static final UUID UUID_GOLF = UUID.fromString("123e4567-e89b-12d3-a456-426614174008");
    public static final UUID UUID_CYCLING = UUID.fromString("123e4567-e89b-12d3-a456-426614174009");
    public static final UUID UUID_SWIMMING = UUID.fromString("123e4567-e89b-12d3-a456-426614174010");
    public static final UUID UUID_ATHLETISM = UUID.fromString("123e4567-e89b-12d3-a456-426614174011");

    @Override
    public void run(String... args) {
        if (catalog.getAllSportTypes().isEmpty()) {
            catalog.save(new SportType(UUID_FOOTBALL, "Football"));
            catalog.save(new SportType(UUID_BASKETBALL, "Basketball"));
            catalog.save(new SportType(UUID_TENNIS, "Tennis"));
            catalog.save(new SportType(UUID_RUGBY, "Rugby"));
            catalog.save(new SportType(UUID_HAND_BALL, "Handball"));
            catalog.save(new SportType(UUID_VOLLEY_BALL, "Volleyball"));
            catalog.save(new SportType(UUID_BASE_BALL, "Baseball"));
            catalog.save(new SportType(UUID_GOLF, "Golf"));
            catalog.save(new SportType(UUID_CYCLING, "Cycling"));
            catalog.save(new SportType(UUID_SWIMMING, "Swimming"));
            catalog.save(new SportType(UUID_ATHLETISM, "Athletism"));
        }
    }
}
