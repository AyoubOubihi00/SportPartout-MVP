package fr.monclub.findclub_service.fixtures;

import fr.monclub.findclub_service.club.model.Club;
import fr.monclub.findclub_service.club.repository.FindClubRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import java.util.UUID;

@Component
@Profile("dev")
public class ClubFixtures implements CommandLineRunner {

    public static final UUID UUID_FOOTBALL = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    public static final UUID UUID_BASKETBALL = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    public static final UUID UUID_TENNIS = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");

    private final FindClubRepository findClubRepository;

    public ClubFixtures(FindClubRepository findClubRepository) {
        this.findClubRepository = findClubRepository;
    }

    private static Date daysAgo(int days) {
        return Date.from(Instant.now().minus(days, ChronoUnit.DAYS));
    }

    @Override
    public void run(String... args) {
        if (findClubRepository.count() == 0) {
            findClubRepository.save(new Club(
                    null,
                    "Club de Football de Paris",
                    this.UUID_FOOTBALL,
                    "football",
                    "Paris",
                    "France",
                    "10 Rue de la Paix",
                    "75002",
                    "0123456789",
                    "parisFC@example.com",
                    48.8566,
                    2.3522,
                    UUID.randomUUID(),
                    daysAgo(30),
                    daysAgo(2),
                    null
            ));
            findClubRepository.save(new Club(
                    null,
                    "Club de Basketball de Lyon",
                    UUID_BASKETBALL,
                    "Basketball",
                    "Lyon",
                    "France",
                    "20 Avenue des Sports",
                    "69003",
                    "0987654321",
                    "bklyon@example.com",
                    45.7640,
                    4.8357,
                    UUID.randomUUID(),
                    daysAgo(70),
                    daysAgo(7),
                    null
            ));
            findClubRepository.save(new Club(
                    null,
                    "Club de Tennis de Marseille",
                    UUID_TENNIS,
                    "Tennis",
                    "Marseille",
                    "France",
                    "5 Boulevard du Littoral",
                    "13001",
                    "0147258369",
                    "tenismarseille@example.com",
                    43.2965,
                    5.3698,
                    UUID.randomUUID(),
                    daysAgo(47),
                    daysAgo(7),
                    null
            ));
        }
    }
}

