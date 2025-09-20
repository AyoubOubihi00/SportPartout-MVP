package fr.monclub.club_service.fixtures;

import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.SportsType;
import fr.monclub.club_service.club.repository.ClubRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@Profile("dev")
public class ClubFixtures implements CommandLineRunner {

    private final ClubRepository clubRepository;

    public static final UUID UUID_FOOTBALL = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    public static final UUID UUID_BASKETBALL = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    public static final UUID UUID_TENNIS = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
    public static final UUID UUID_OWNER1 = UUID.fromString("223e4567-e89b-12d3-a456-426614174001");


    public ClubFixtures(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public void run(String... args) {
        if (clubRepository.count() == 0) {
            clubRepository.save(new Club(
                    null,
                    "Club de Football de Paris",
                    UUID_FOOTBALL,
                    "football",
                    "Paris",
                    "France",
                    "10 Rue de la Paix",
                    "75002",
                    "0123456789",
                    "parisFC@example.com",
                    49.8566,
                    2.3522,
                    UUID_OWNER1,
                    new Date(),
                    new Date(),
                    null,
                    true
            ));
            clubRepository.save(new Club(
                    null,
                    "Club de Basketball de Lyon",
                    UUID_BASKETBALL,
                    "basketball",
                    "Lyon",
                    "France",
                    "20 Avenue des Sports",
                    "69003",
                    "0987654321",
                    "bklyon@example.com",
                    45.7640,
                    4.8357,
                    UUID_OWNER1,
                    new Date(),
                    new Date(),
                    null,
                    true
            ));
            clubRepository.save(new Club(
                    null,
                    "Club de Tennis de Marseille",
                    UUID_TENNIS,
                    "tennis",
                    "Marseille",
                    "France",
                    "5 Boulevard du Littoral",
                    "13001",
                    "0147258369",
                    "tenismarseille@example.com",
                    43.2965,
                    5.3698,
                    UUID_OWNER1,
                    new Date(),
                    new Date(),
                    null,
                    true
            ));
        }
    }
}
