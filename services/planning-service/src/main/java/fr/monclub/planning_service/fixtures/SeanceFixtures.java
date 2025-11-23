package fr.monclub.planning_service.fixtures;

import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import fr.monclub.planning_service.planning.repository.SeanceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Profile({"dev", "prod"})
public class SeanceFixtures implements CommandLineRunner {

    public static final UUID CLUB_PARIS = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1");
    public static final UUID CLUB_LYON = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2");
    // IDs alignes sur les comptes gerants seedes dans auth-service (UserFixtures)
    public static final UUID USER_PARIS = UUID.fromString("666e2278-77e2-48e6-b724-af3e4727ed7f");
    public static final UUID USER_LYON = UUID.fromString("d505d902-5519-53cf-8ab6-535b3c2e8d4b");
    public static final UUID USER_MARSEILLE = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID USER_METZ = UUID.fromString("3dc50597-863b-45fe-a442-6dbdcde4ddaf");
    public static final UUID USER_NANCY = UUID.fromString("3dc50597-863b-45fe-a442-6dbdcde4ddaf");
    public static final UUID USER_STRASBOURG = UUID.fromString("d349cfd1-a1f3-4226-bc36-36b72d9a43e0");
    public static final UUID TERRAIN_1 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1");
    public static final String TERRAIN_NAME_1 = "Terrain Salle Europe";
    public static final UUID TERRAIN_2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2");
    public static final String TERRAIN_NAME_2 = "Terrain Salle Alsace";
    public static final UUID COACH_ALICE = UUID.fromString("cccccccc-cccc-cccc-cccc-ccccccccccc1");
    public static final UUID COACH_BOB = UUID.fromString("cccccccc-cccc-cccc-cccc-ccccccccccc2");
    public static final UUID SPORT_FOOT = UUID.fromString("dddddddd-dddd-dddd-dddd-ddddddddddd1");
    public static final UUID SPORT_TENNIS = UUID.fromString("dddddddd-dddd-dddd-dddd-ddddddddddd2");
    public static final UUID SPORT_BASKET = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    public static final UUID SPORT_NATATION = UUID.fromString("123e4567-e89b-12d3-a456-426614174012");
    public static final UUID SPORT_VOLLEY = UUID.fromString("123e4567-e89b-12d3-a456-426614174014");
    public static final String BOB = "Bob Bobson";
    public static final String ALICE = "Alice Aliceton";

    private final SeanceRepository repo;

    public SeanceFixtures(SeanceRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) {
            return; // deja rempli
        }

        OffsetDateTime now = OffsetDateTime.now();

        // Seance de foot aujourd'hui 18h-19h a Paris (terrain 1, coach Alice) avec 3 participants
        Seance s1 = Seance.builder()
                .idClub(CLUB_PARIS)
                .idTerrain(TERRAIN_1)
                .clubName("Paris Sports Club")
                .terrainName(TERRAIN_NAME_1)
                .idCoach(COACH_ALICE)
                .coachName(ALICE)
                .citySeance("Paris")
                .adressSeance("10 rue des Lilas, 75000 Paris")
                .startTime(now.withHour(18).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.withHour(19).withMinute(0).withSecond(0).withNano(0))
                .capacite(20)
                .capaSeance(2)
                .participants(Set.of(USER_PARIS, USER_LYON))
                .idSport(SPORT_FOOT)
                .nameSport("Football")
                .description("Seance decouverte, niveaux mixtes")
                .status(SeanceStatus.OPEN)
                .createdBy(UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1")) // gerant
                .build();

        // Seance de tennis demain 10h-11h a Paris (terrain 2, coach Bob) complet avec 4 participants
        Seance s2 = Seance.builder()
                .idClub(CLUB_PARIS)
                .clubName("Paris Sports Club")
                .idTerrain(TERRAIN_2)
                .terrainName(TERRAIN_NAME_2)
                .idCoach(COACH_BOB)
                .coachName(BOB)
                .citySeance("Paris")
                .adressSeance("15 avenue des Champs, 75000 Paris")
                .startTime(now.plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .capacite(4)
                .capaSeance(4) // complet
                .participants(Set.of(USER_PARIS, USER_LYON, USER_NANCY, USER_STRASBOURG))
                .idSport(SPORT_TENNIS)
                .nameSport("Tennis")
                .description("Cours collectif intermediaire")
                .status(SeanceStatus.FULL)
                .createdBy(UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1"))
                .build();

        // Seance de foot a Lyon ce week-end (terrain 1, coach Alice) - en attente (PENDING) avec 2 inscrits
        Seance s3 = Seance.builder()
                .idClub(CLUB_LYON)
                .idTerrain(TERRAIN_1)
                .clubName("Lyon Sports Club")
                .terrainName(TERRAIN_NAME_1)
                .idCoach(COACH_ALICE)
                .coachName(ALICE)
                .adressSeance("10 rue des Lilas, 69000 Lyon")
                .citySeance("Lyon")
                .startTime(now.plusDays(2).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.plusDays(2).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .capacite(16)
                .capaSeance(2)
                .participants(Set.of(USER_LYON, USER_NANCY))
                .idSport(SPORT_FOOT)
                .nameSport("Football")
                .description("Match amical, validation du gerant requise")
                .status(SeanceStatus.PENDING)
                .createdBy(UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff1")) // cree par user
                .build();

        // Seance annulee (exemple de statut CANCELED)
        Seance s4 = Seance.builder()
                .idClub(CLUB_LYON)
                .clubName("Lyon Sports Club")
                .idTerrain(TERRAIN_2)
                .terrainName(TERRAIN_NAME_2)
                .idCoach(COACH_BOB)
                .coachName(BOB)
                .adressSeance("10 rue des Lilas, 69000 Lyon")
                .citySeance("Lyon")
                .startTime(now.minusDays(1).withHour(17).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.minusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0))
                .capacite(10)
                .capaSeance(0)
                .participants(Set.of())
                .idSport(SPORT_TENNIS)
                .nameSport("Tennis")
                .description("Session annulee (meteo)")
                .status(SeanceStatus.CANCELED)
                .createdBy(UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1"))
                .build();

        // Seance basket Metz (future) avec un premier inscrit
        Seance s5 = Seance.builder()
                .idClub(UUID.fromString("89a23eef-7f22-4b46-bf8f-47d5cbec8013"))
                .clubName("Metz Basket Club")
                .idTerrain(TERRAIN_2)
                .terrainName(TERRAIN_NAME_2)
                .idCoach(COACH_BOB)
                .coachName(BOB)
                .adressSeance("123 avenue de la Republique, 57000 Metz")
                .citySeance("Metz")
                .startTime(now.plusDays(1).withHour(17).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.plusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0))
                .capacite(10)
                .capaSeance(1)
                .participants(Set.of(USER_METZ))
                .idSport(SPORT_BASKET)
                .nameSport("Basketball")
                .description("Ouverture nouvelle seance")
                .status(SeanceStatus.OPEN)
                .createdBy(UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1"))
                .build();

        // Metz football (future) avec USER_METZ
        Seance s9 = Seance.builder()
                .idClub(UUID.fromString("9ccb204c-2cdb-444c-b42b-296891d1e17a"))
                .clubName("Football Club Metz Sud")
                .idTerrain(TERRAIN_1)
                .terrainName(TERRAIN_NAME_1)
                .idCoach(COACH_ALICE)
                .coachName(ALICE)
                .adressSeance("20 Avenue Foch, 57000 Metz")
                .citySeance("Metz")
                .startTime(now.plusDays(2).withHour(18).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.plusDays(2).withHour(19).withMinute(30).withSecond(0).withNano(0))
                .capacite(16)
                .capaSeance(1)
                .participants(Set.of(USER_METZ))
                .idSport(SPORT_FOOT)
                .nameSport("Football")
                .description("Entrainement soir, niveau loisir")
                .status(SeanceStatus.OPEN)
                .createdBy(USER_METZ)
                .build();

        // Metz tennis (passee) avec USER_METZ et USER_PARIS
        Seance s10 = Seance.builder()
                .idClub(UUID.fromString("2c4e6f8a-3b5d-4c9e-9f1a-7e2b8c9d5f4e"))
                .clubName("Club de Tennis de Metz")
                .idTerrain(TERRAIN_2)
                .terrainName(TERRAIN_NAME_2)
                .idCoach(COACH_BOB)
                .coachName(BOB)
                .adressSeance("12 Rue de la Chevre, 57000 Metz")
                .citySeance("Metz")
                .startTime(now.minusDays(3).withHour(9).withMinute(30).withSecond(0).withNano(0))
                .endTime(now.minusDays(3).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .capacite(6)
                .capaSeance(2)
                .participants(Set.of(USER_METZ, USER_PARIS))
                .idSport(SPORT_TENNIS)
                .nameSport("Tennis")
                .description("Cours passe, perfectionnement")
                .status(SeanceStatus.OPEN)
                .createdBy(USER_METZ)
                .build();

        // Metz natation (passee) avec USER_METZ
        Seance s13 = Seance.builder()
                .idClub(UUID.fromString("db3ce306-52d9-4d8f-81ae-c579b44113c3"))
                .clubName("Club de Natation Metz Plage")
                .idTerrain(TERRAIN_1)
                .terrainName(TERRAIN_NAME_1)
                .idCoach(COACH_ALICE)
                .coachName(ALICE)
                .adressSeance("3 Rue du Pontiffroy, 57000 Metz")
                .citySeance("Metz")
                .startTime(now.minusDays(6).withHour(7).withMinute(30).withSecond(0).withNano(0))
                .endTime(now.minusDays(6).withHour(9).withMinute(0).withSecond(0).withNano(0))
                .capacite(12)
                .capaSeance(1)
                .participants(Set.of(USER_METZ))
                .idSport(SPORT_NATATION)
                .nameSport("Natation")
                .description("Entrainement matinal passe")
                .status(SeanceStatus.OPEN)
                .createdBy(USER_METZ)
                .build();

        // Strasbourg tennis (passee) avec USER_STRASBOURG et USER_PARIS
        Seance s11 = Seance.builder()
                .idClub(UUID.fromString("defb7a66-7ef3-47a3-b205-bee13c580619"))
                .clubName("Strasbourg Tennis Club")
                .idTerrain(TERRAIN_1)
                .terrainName(TERRAIN_NAME_1)
                .idCoach(COACH_BOB)
                .coachName(BOB)
                .adressSeance("2 Allee du Printemps, 67000 Strasbourg")
                .citySeance("Strasbourg")
                .startTime(now.minusDays(5).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.minusDays(5).withHour(15).withMinute(30).withSecond(0).withNano(0))
                .capacite(8)
                .capaSeance(2)
                .participants(Set.of(USER_STRASBOURG, USER_PARIS))
                .idSport(SPORT_TENNIS)
                .nameSport("Tennis")
                .description("Session passe, travail de service")
                .status(SeanceStatus.OPEN)
                .createdBy(USER_STRASBOURG)
                .build();

        // Nancy volley (passee) avec USER_NANCY
        Seance s12 = Seance.builder()
                .idClub(UUID.fromString("9459cae6-cc8c-411d-8c89-0feac8348943"))
                .clubName("Nancy Volley Ball")
                .idTerrain(TERRAIN_2)
                .terrainName(TERRAIN_NAME_2)
                .idCoach(COACH_ALICE)
                .coachName(ALICE)
                .adressSeance("5 Rue Stanislas, 54000 Nancy")
                .citySeance("Nancy")
                .startTime(now.minusDays(4).withHour(18).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.minusDays(4).withHour(19).withMinute(30).withSecond(0).withNano(0))
                .capacite(14)
                .capaSeance(1)
                .participants(Set.of(USER_NANCY))
                .idSport(SPORT_VOLLEY)
                .nameSport("Volley")
                .description("Match passe, niveau loisir")
                .status(SeanceStatus.OPEN)
                .createdBy(USER_NANCY)
                .build();

        // Nancy football (future) avec 2 inscrits
        Seance s6 = Seance.builder()
                .idClub(UUID.fromString("0fa59129-3bc4-427e-93f1-f95cf0f8edec"))
                .clubName("Nancy Football Club")
                .idTerrain(TERRAIN_1)
                .terrainName(TERRAIN_NAME_1)
                .idCoach(COACH_ALICE)
                .coachName(ALICE)
                .adressSeance("22 Avenue de Boufflers, 54000 Nancy")
                .citySeance("Nancy")
                .startTime(now.plusDays(3).withHour(18).withMinute(30).withSecond(0).withNano(0))
                .endTime(now.plusDays(3).withHour(20).withMinute(0).withSecond(0).withNano(0))
                .capacite(18)
                .capaSeance(2)
                .participants(Set.of(USER_NANCY, USER_PARIS))
                .idSport(SPORT_FOOT)
                .nameSport("Football")
                .description("Match amical en soiree")
                .status(SeanceStatus.OPEN)
                .createdBy(USER_NANCY)
                .build();

        // Nancy tennis (passee) avec 1 inscrit
        Seance s7 = Seance.builder()
                .idClub(UUID.fromString("3517ebf3-51e1-436a-9a09-0e78f51cce70"))
                .clubName("Nancy Tennis Club")
                .idTerrain(TERRAIN_2)
                .terrainName(TERRAIN_NAME_2)
                .idCoach(COACH_BOB)
                .coachName(BOB)
                .adressSeance("10 Rue Saint-Dizier, 54000 Nancy")
                .citySeance("Nancy")
                .startTime(now.minusDays(2).withHour(9).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.minusDays(2).withHour(10).withMinute(30).withSecond(0).withNano(0))
                .capacite(6)
                .capaSeance(1)
                .participants(Set.of(USER_NANCY))
                .idSport(SPORT_TENNIS)
                .nameSport("Tennis")
                .description("Cours passe, groupe reduit")
                .status(SeanceStatus.OPEN)
                .createdBy(USER_NANCY)
                .build();

        // Strasbourg basket (future) avec 3 inscrits
        Seance s8 = Seance.builder()
                .idClub(UUID.fromString("77101f86-7da5-48f0-ae85-b3920b1a91cf"))
                .clubName("Strasbourg Basket Union")
                .idTerrain(TERRAIN_1)
                .terrainName(TERRAIN_NAME_1)
                .idCoach(COACH_BOB)
                .coachName(BOB)
                .adressSeance("16 Rue des Juifs, 67000 Strasbourg")
                .citySeance("Strasbourg")
                .startTime(now.plusDays(4).withHour(19).withMinute(0).withSecond(0).withNano(0))
                .endTime(now.plusDays(4).withHour(20).withMinute(30).withSecond(0).withNano(0))
                .capacite(12)
                .capaSeance(3)
                .participants(Set.of(USER_STRASBOURG, USER_PARIS, USER_LYON))
                .idSport(SPORT_BASKET)
                .nameSport("Basketball")
                .description("Entrainement competiteurs")
                .status(SeanceStatus.OPEN)
                .createdBy(USER_STRASBOURG)
                .build();

        repo.saveAll(List.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13));
    }
}
