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
@Profile("prod")
public class ClubFixtures implements CommandLineRunner {

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
    public static final UUID UUID_NATATION = UUID.fromString("123e4567-e89b-12d3-a456-426614174012");
    public static final UUID UUID_JUDO = UUID.fromString("123e4567-e89b-12d3-a456-426614174013");
    public static final UUID UUID_VOLLEY = UUID.fromString("123e4567-e89b-12d3-a456-426614174014");


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
                    UUID.fromString("0f609278-76d5-4a27-81d5-d2a7a63594f0"),
                    "Club de Football de Paris",
                    4.5,
                    12,
                    UUID_FOOTBALL,
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
                    UUID.fromString("76f392b1-ed0f-4ad3-ae6b-631574d634b1"),
                    "Club de Basketball de Lyon",
                    4.0,
                    22,
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
                    UUID.fromString("1b7c8461-8d2c-4540-83ca-1eab47b092bd"),
                    "Club de Tennis de Marseille",
                    3.8,
                    9,
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
            findClubRepository.save(new Club(
                    UUID.fromString("2c4e6f8a-3b5d-4c9e-9f1a-7e2b8c9d5f4e"),
                    "Club de Tennis de Metz",
                    4.2,
                    21,
                    UUID_TENNIS,
                    "Tennis",
                    "Metz",
                    "France",
                    "12 Rue de la Chèvre",
                    "57000",
                    "0387551234",
                    "tennismetz@example.com",
                    49.1193,
                    6.1757,
                    UUID.randomUUID(),
                    daysAgo(35),
                    daysAgo(5),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("89a23eef-7f22-4b46-bf8f-47d5cbec8013"),
                    "Metz Basket Club",
                    4.3,
                    31,
                    UUID_BASKETBALL,
                    "Basketball",
                    "Metz",
                    "France",
                    "8 Rue Serpenoise",
                    "57000",
                    "0387555678",
                    "basketmetz@example.com",
                    49.1208,
                    6.1761,
                    UUID.randomUUID(),
                    daysAgo(60),
                    daysAgo(10),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("9ccb204c-2cdb-444c-b42b-296891d1e17a"),
                    "Football Club Metz Sud",
                    2.9,
                    29,
                    UUID_FOOTBALL,
                    "Football",
                    "Metz",
                    "France",
                    "20 Avenue Foch",
                    "57000",
                    "0387569988",
                    "fcmetzsud@example.com",
                    49.1185,
                    6.1750,
                    UUID.randomUUID(),
                    daysAgo(22),
                    daysAgo(3),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("db3ce306-52d9-4d8f-81ae-c579b44113c3"),
                    "Club de Natation Metz Plage",
                    4.1,
                    45,
                    UUID_NATATION,
                    "Natation",
                    "Metz",
                    "France",
                    "3 Rue du Pontiffroy",
                    "57000",
                    "0387562222",
                    "natmetz@example.com",
                    49.1221,
                    6.1782,
                    UUID.randomUUID(),
                    daysAgo(75),
                    daysAgo(12),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("37323ae1-3875-4fec-a648-80aaad25fe9c"),
                    "Metz Judo Académie",
                    4.6,
                    7,
                    UUID_JUDO,
                    "Judo",
                    "Metz",
                    "France",
                    "15 Rue du Cambout",
                    "57000",
                    "0387591111",
                    "judometz@example.com",
                    49.1177,
                    6.1768,
                    UUID.randomUUID(),
                    daysAgo(40),
                    daysAgo(9),
                    null
            ));
            findClubRepository.save(new Club(
                    UUID.fromString("3517ebf3-51e1-436a-9a09-0e78f51cce70"),
                    "Nancy Tennis Club",
                    4.4,
                    12,
                    UUID_TENNIS,
                    "Tennis",
                    "Nancy",
                    "France",
                    "10 Rue Saint-Dizier",
                    "54000",
                    "0383271111",
                    "tennisnancy@example.com",
                    48.6921,
                    6.1844,
                    UUID.randomUUID(),
                    daysAgo(33),
                    daysAgo(6),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("9459cae6-cc8c-411d-8c89-0feac8348943"),
                    "Nancy Volley Ball",
                    4.0,
                    45,
                    UUID_VOLLEY,
                    "Volley",
                    "Nancy",
                    "France",
                    "5 Rue Stanislas",
                    "54000",
                    "0383272222",
                    "volleynancy@example.com",
                    48.6920,
                    6.1833,
                    UUID.randomUUID(),
                    daysAgo(52),
                    daysAgo(8),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("0fa59129-3bc4-427e-93f1-f95cf0f8edec"),
                    "Nancy Football Club",
                    3.9,
                    25,
                    UUID_FOOTBALL,
                    "Football",
                    "Nancy",
                    "France",
                    "22 Avenue de Boufflers",
                    "54000",
                    "0383283333",
                    "fcnancy@example.com",
                    48.6915,
                    6.1772,
                    UUID.randomUUID(),
                    daysAgo(45),
                    daysAgo(2),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("f697f6dd-f16b-4057-94e2-dce34297180b"),
                    "Nancy Natation Loisirs",
                    4.2,
                    30,
                    UUID_NATATION,
                    "Natation",
                    "Nancy",
                    "France",
                    "9 Rue de la Commanderie",
                    "54000",
                    "0383284444",
                    "natationnancy@example.com",
                    48.6909,
                    6.1820,
                    UUID.randomUUID(),
                    daysAgo(25),
                    daysAgo(4),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("7f03572a-2f59-452e-9000-27868130a177"),
                    "Nancy Judo Club",
                    2.8,
                    35,
                    UUID_JUDO,
                    "Judo",
                    "Nancy",
                    "France",
                    "14 Rue du Faubourg des Trois Maisons",
                    "54000",
                    "0383285555",
                    "judonancy@example.com",
                    48.6924,
                    6.1769,
                    UUID.randomUUID(),
                    daysAgo(70),
                    daysAgo(11),
                    null
            ));
            findClubRepository.save(new Club(
                    UUID.fromString("defb7a66-7ef3-47a3-b205-bee13c580619"),
                    "Strasbourg Tennis Club",
                    3.7,
                    18,
                    UUID_TENNIS,
                    "Tennis",
                    "Strasbourg",
                    "France",
                    "2 Allée du Printemps",
                    "67000",
                    "0388251111",
                    "tennisstrasbourg@example.com",
                    48.5734,
                    7.7521,
                    UUID.randomUUID(),
                    daysAgo(38),
                    daysAgo(5),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("77101f86-7da5-48f0-ae85-b3920b1a91cf"),
                    "Strasbourg Basket Union",
                    4.5,
                    27,
                    UUID_BASKETBALL,
                    "Basketball",
                    "Strasbourg",
                    "France",
                    "16 Rue des Juifs",
                    "67000",
                    "0388252222",
                    "basketstrasbourg@example.com",
                    48.5740,
                    7.7512,
                    UUID.randomUUID(),
                    daysAgo(41),
                    daysAgo(6),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("a0548458-9809-4071-a497-1945e830b2dc"),
                    "Racing Club de Strasbourg",
                    4.8,
                    15,
                    UUID_FOOTBALL,
                    "Football",
                    "Strasbourg",
                    "France",
                    "12 Boulevard de Dresde",
                    "67000",
                    "0388263333",
                    "rcstrasbourg@example.com",
                    48.5780,
                    7.7519,
                    UUID.randomUUID(),
                    daysAgo(18),
                    daysAgo(2),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("d9f93ea6-a6ba-4a2f-8ea7-930253108072"),
                    "Strasbourg Natation",
                    2.9,
                    40,
                    UUID_NATATION,
                    "Natation",
                    "Strasbourg",
                    "France",
                    "5 Rue Pierre Montet",
                    "67000",
                    "0388274444",
                    "natationstrasbourg@example.com",
                    48.5765,
                    7.7541,
                    UUID.randomUUID(),
                    daysAgo(29),
                    daysAgo(4),
                    null
            ));

            findClubRepository.save(new Club(
                    UUID.fromString("07d16769-4ff3-4195-b65f-6b22a31a03ac"),
                    "Strasbourg Judo Académie",
                    1.9,
                    20,
                    UUID_JUDO,
                    "Judo",
                    "Strasbourg",
                    "France",
                    "10 Rue du Dôme",
                    "67000",
                    "0388275555",
                    "judostrasbourg@example.com",
                    48.5750,
                    7.7509,
                    UUID.randomUUID(),
                    daysAgo(55),
                    daysAgo(9),
                    null
            ));



        }
    }
}

