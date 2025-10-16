package fr.monclub.planning_service.fixtures;

import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import fr.monclub.planning_service.planning.repository.SeanceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Profile("dev")
public class SeanceFixtures implements CommandLineRunner {

    public static final UUID CLUB_PARIS   = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1");
    public static final UUID CLUB_LYON    = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2");
    public static final UUID TERRAIN_1    = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1");
    public static final UUID TERRAIN_2    = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2");
    public static final UUID COACH_ALICE  = UUID.fromString("cccccccc-cccc-cccc-cccc-ccccccccccc1");
    public static final UUID COACH_BOB    = UUID.fromString("cccccccc-cccc-cccc-cccc-ccccccccccc2");
    public static final UUID SPORT_FOOT   = UUID.fromString("dddddddd-dddd-dddd-dddd-ddddddddddd1");
    public static final UUID SPORT_TENNIS = UUID.fromString("dddddddd-dddd-dddd-dddd-ddddddddddd2");

    private final SeanceRepository repo;

    public SeanceFixtures(SeanceRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return; // déjà rempli

        OffsetDateTime now = OffsetDateTime.now();

        // Séance de foot aujourd’hui 18h–19h à Paris (terrain 1, coach Alice)
        Seance s1 = Seance.builder()
                .idClub(CLUB_PARIS)
                .idTerrain(TERRAIN_1)
                .idCoach(COACH_ALICE)
                .startTime(now.withHour(18).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.withHour(19).withMinute(0).withSecond(0).withNano(0))
                .capacite(20)
                .capaSeance(5)
                .idSport(SPORT_FOOT)
                .nameSport("Football")
                .description("Séance découverte – niveaux mixtes")
                .status(SeanceStatus.OPEN)
                .createdBy(UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1")) // gérant
                .build();

        // Séance de tennis demain 10h–11h à Paris (terrain 2, coach Bob)
        Seance s2 = Seance.builder()
                .idClub(CLUB_PARIS)
                .idTerrain(TERRAIN_2)
                .idCoach(COACH_BOB)
                .startTime(now.plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .capacite(4)
                .capaSeance(4) // complet
                .idSport(SPORT_TENNIS)
                .nameSport("Tennis")
                .description("Cours collectif – intermédiaire")
                .status(SeanceStatus.FULL)
                .createdBy(UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1"))
                .build();

        // Séance de foot à Lyon ce week-end (terrain 1, coach Alice) – en attente (PENDING)
        Seance s3 = Seance.builder()
                .idClub(CLUB_LYON)
                .idTerrain(TERRAIN_1)
                .idCoach(COACH_ALICE)
                .startTime(now.plusDays(2).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.plusDays(2).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .capacite(16)
                .capaSeance(2)
                .idSport(SPORT_FOOT)
                .nameSport("Football")
                .description("Match amical – validation du gérant requise")
                .status(SeanceStatus.PENDING)
                .createdBy(UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff1")) // créé par user
                .build();

        // Séance annulée (exemple de statut CANCELED)
        Seance s4 = Seance.builder()
                .idClub(CLUB_LYON)
                .idTerrain(TERRAIN_2)
                .idCoach(COACH_BOB)
                .startTime(now.minusDays(1).withHour(17).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.minusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0))
                .capacite(10)
                .capaSeance(0)
                .idSport(SPORT_TENNIS)
                .nameSport("Tennis")
                .description("Session annulée (météo)")
                .status(SeanceStatus.CANCELED)
                .createdBy(UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1"))
                .build();

        repo.saveAll(List.of(s1, s2, s3, s4));
    }
}
