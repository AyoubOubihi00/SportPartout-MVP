package fr.monclub.club_membership_service.membership.fixtures;

import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Profile("prod")
public class Fixture implements CommandLineRunner {

    private static final Map<UUID, String> SPORT_LABELS = Map.ofEntries(
            Map.entry(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "Football"),
            Map.entry(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"), "Basketball"),
            Map.entry(UUID.fromString("123e4567-e89b-12d3-a456-426614174003"), "Tennis"),
            Map.entry(UUID.fromString("123e4567-e89b-12d3-a456-426614174012"), "Natation"),
            Map.entry(UUID.fromString("123e4567-e89b-12d3-a456-426614174013"), "Judo"),
            Map.entry(UUID.fromString("123e4567-e89b-12d3-a456-426614174014"), "Volley")
    );

    private static final UUID OWNER_PARIS = UUID.fromString("3eb49b4e-762d-5022-b61c-c52c2dc414db");
    private static final UUID OWNER_LYON = UUID.fromString("d505d902-5519-53cf-8ab6-535b3c2e8d4b");
    private static final UUID OWNER_MARSEILLE = UUID.fromString("1e0cd442-27b2-5e5d-a7a1-e55a72c2b704");
    private static final UUID OWNER_METZ = UUID.fromString("8e71c78c-ec27-5dfe-bf57-308eb1878d6f");
    private static final UUID OWNER_NANCY = UUID.fromString("95fb5d0a-a8f3-573a-93b3-4b9eb6bae808");
    private static final UUID OWNER_STRASBOURG = UUID.fromString("5c69ecdd-85e9-5f65-9b1c-6487808ee449");

    private static final Map<String, UUID> OWNER_BY_CITY = Map.ofEntries(
            Map.entry("paris", OWNER_PARIS),
            Map.entry("lyon", OWNER_LYON),
            Map.entry("marseille", OWNER_MARSEILLE),
            Map.entry("metz", OWNER_METZ),
            Map.entry("nancy", OWNER_NANCY),
            Map.entry("strasbourg", OWNER_STRASBOURG)
    );

    private static final List<String> FIRST_NAMES = List.of(
            "Camille",
            "Louis",
            "Sophie",
            "Nadia",
            "Julien",
            "Manon",
            "Alexandre",
            "Lina",
            "Thomas",
            "Margot",
            "Hugo",
            "Adrien"
    );

    private static final List<String> LAST_NAMES = List.of(
            "Bernard",
            "Martin",
            "Laurent",
            "Becker",
            "Dubois",
            "Moreau",
            "Lefevre",
            "Petit",
            "Garcia",
            "Blanc",
            "Marchand",
            "Renard"
    );

    private static final List<ClubSeed> CLUBS = List.of(
            new ClubSeed(
                    UUID.fromString("0f609278-76d5-4a27-81d5-d2a7a63594f0"),
                    "Club de Football de Paris",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                    "Paris",
                    "France",
                    "99 Boulevard Kellermann",
                    "75013",
                    "+33 1 44 16 31 31",
                    "contact@paris-footclub.fr",
                    48.8210,
                    2.3553,
                    4.7,
                    220,
                    180,
                    7
            ),
            new ClubSeed(
                    UUID.fromString("76f392b1-ed0f-4ad3-ae6b-631574d634b1"),
                    "Club de Basketball de Lyon",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174002"),
                    "Lyon",
                    "France",
                    "5 Avenue Simone Veil",
                    "69150",
                    "+33 4 72 82 33 00",
                    "contact@lyon-basket.fr",
                    45.7688,
                    4.9575,
                    4.4,
                    185,
                    210,
                    10
            ),
            new ClubSeed(
                    UUID.fromString("1b7c8461-8d2c-4540-83ca-1eab47b092bd"),
                    "Club de Tennis de Marseille",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174003"),
                    "Marseille",
                    "France",
                    "5 Boulevard du Littoral",
                    "13001",
                    "+33 4 91 55 70 00",
                    "contact@marseille-tennis.fr",
                    43.2969,
                    5.3697,
                    4.1,
                    120,
                    160,
                    12
            ),
            new ClubSeed(
                    UUID.fromString("2c4e6f8a-3b5d-4c9e-9f1a-7e2b8c9d5f4e"),
                    "Club de Tennis de Metz",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174003"),
                    "Metz",
                    "France",
                    "12 Rue de la Chevre",
                    "57000",
                    "+33 3 87 75 12 34",
                    "contact@metz-tennis.fr",
                    49.1193,
                    6.1757,
                    4.2,
                    95,
                    140,
                    14
            ),
            new ClubSeed(
                    UUID.fromString("89a23eef-7f22-4b46-bf8f-47d5cbec8013"),
                    "Metz Basket Club",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174002"),
                    "Metz",
                    "France",
                    "8 Rue Serpenoise",
                    "57000",
                    "+33 3 87 75 56 78",
                    "contact@metz-basket.fr",
                    49.1208,
                    6.1761,
                    4.5,
                    175,
                    190,
                    6
            ),
            new ClubSeed(
                    UUID.fromString("9ccb204c-2cdb-444c-b42b-296891d1e17a"),
                    "Football Club Metz Sud",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                    "Metz",
                    "France",
                    "3 Allee Saint-Symphorien",
                    "57050",
                    "+33 3 87 65 75 70",
                    "contact@metz-foot.fr",
                    49.1118,
                    6.1549,
                    4.0,
                    210,
                    220,
                    5
            ),
            new ClubSeed(
                    UUID.fromString("db3ce306-52d9-4d8f-81ae-c579b44113c3"),
                    "Club de Natation Metz Plage",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174012"),
                    "Metz",
                    "France",
                    "3 Rue du Pontiffroy",
                    "57000",
                    "+33 3 87 56 22 22",
                    "contact@metz-natation.fr",
                    49.1221,
                    6.1782,
                    4.3,
                    130,
                    150,
                    9
            ),
            new ClubSeed(
                    UUID.fromString("37323ae1-3875-4fec-a648-80aaad25fe9c"),
                    "Metz Judo Academie",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174013"),
                    "Metz",
                    "France",
                    "15 Rue du Cambout",
                    "57000",
                    "+33 3 87 59 11 11",
                    "contact@metz-judo.fr",
                    49.1177,
                    6.1768,
                    4.0,
                    70,
                    130,
                    8
            ),
            new ClubSeed(
                    UUID.fromString("3517ebf3-51e1-436a-9a09-0e78f51cce70"),
                    "Nancy Tennis Club",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174003"),
                    "Nancy",
                    "France",
                    "10 Rue Saint-Dizier",
                    "54000",
                    "+33 3 83 27 11 11",
                    "contact@nancy-tennis.fr",
                    48.6921,
                    6.1844,
                    4.2,
                    110,
                    120,
                    6
            ),
            new ClubSeed(
                    UUID.fromString("9459cae6-cc8c-411d-8c89-0feac8348943"),
                    "Nancy Volley Ball",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174014"),
                    "Nancy",
                    "France",
                    "5 Rue Stanislas",
                    "54000",
                    "+33 3 83 27 22 22",
                    "contact@nancy-volley.fr",
                    48.6920,
                    6.1833,
                    3.9,
                    90,
                    150,
                    9
            ),
            new ClubSeed(
                    UUID.fromString("0fa59129-3bc4-427e-93f1-f95cf0f8edec"),
                    "Nancy Football Club",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                    "Nancy",
                    "France",
                    "22 Avenue de Boufflers",
                    "54000",
                    "+33 3 83 28 33 33",
                    "contact@nancy-foot.fr",
                    48.6915,
                    6.1772,
                    4.1,
                    200,
                    210,
                    4
            ),
            new ClubSeed(
                    UUID.fromString("f697f6dd-f16b-4057-94e2-dce34297180b"),
                    "Nancy Natation Loisirs",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174012"),
                    "Nancy",
                    "France",
                    "9 Rue de la Commanderie",
                    "54000",
                    "+33 3 83 28 44 44",
                    "contact@nancy-natation.fr",
                    48.6909,
                    6.1820,
                    4.0,
                    85,
                    180,
                    7
            ),
            new ClubSeed(
                    UUID.fromString("7f03572a-2f59-452e-9000-27868130a177"),
                    "Nancy Judo Club",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174013"),
                    "Nancy",
                    "France",
                    "14 Rue du Faubourg des Trois Maisons",
                    "54000",
                    "+33 3 83 28 55 55",
                    "contact@nancy-judo.fr",
                    48.6924,
                    6.1769,
                    3.8,
                    65,
                    200,
                    11
            ),
            new ClubSeed(
                    UUID.fromString("defb7a66-7ef3-47a3-b205-bee13c580619"),
                    "Strasbourg Tennis Club",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174003"),
                    "Strasbourg",
                    "France",
                    "2 Allee du Printemps",
                    "67000",
                    "+33 3 88 25 11 11",
                    "contact@strasbourg-tennis.fr",
                    48.5734,
                    7.7521,
                    4.3,
                    150,
                    160,
                    5
            ),
            new ClubSeed(
                    UUID.fromString("77101f86-7da5-48f0-ae85-b3920b1a91cf"),
                    "Strasbourg Basket Union",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174002"),
                    "Strasbourg",
                    "France",
                    "16 Rue des Juifs",
                    "67000",
                    "+33 3 88 25 22 22",
                    "contact@strasbourg-basket.fr",
                    48.5740,
                    7.7512,
                    4.6,
                    180,
                    190,
                    6
            ),
            new ClubSeed(
                    UUID.fromString("a0548458-9809-4071-a497-1945e830b2dc"),
                    "Racing Club de Strasbourg",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                    "Strasbourg",
                    "France",
                    "12 Boulevard de Dresde",
                    "67000",
                    "+33 3 88 26 33 33",
                    "contact@rcstrasbourg.fr",
                    48.5780,
                    7.7519,
                    4.8,
                    240,
                    220,
                    3
            ),
            new ClubSeed(
                    UUID.fromString("d9f93ea6-a6ba-4a2f-8ea7-930253108072"),
                    "Strasbourg Natation",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174012"),
                    "Strasbourg",
                    "France",
                    "5 Rue Pierre Montet",
                    "67000",
                    "+33 3 88 27 44 44",
                    "contact@strasbourg-natation.fr",
                    48.5765,
                    7.7541,
                    4.1,
                    115,
                    170,
                    4
            ),
            new ClubSeed(
                    UUID.fromString("07d16769-4ff3-4195-b65f-6b22a31a03ac"),
                    "Strasbourg Judo Academie",
                    UUID.fromString("123e4567-e89b-12d3-a456-426614174013"),
                    "Strasbourg",
                    "France",
                    "10 Rue du Dome",
                    "67000",
                    "+33 3 88 27 55 55",
                    "contact@strasbourg-judo.fr",
                    48.5750,
                    7.7509,
                    3.7,
                    55,
                    185,
                    9
            )
    );

    private static final Map<UUID, ClubSeed> CLUBS_BY_ID =
            CLUBS.stream().collect(Collectors.toUnmodifiableMap(ClubSeed::id, Function.identity()));

    private static final List<MemberSeed> MEMBERS = generateMemberSeeds();

    private static List<MemberSeed> generateMemberSeeds() {
        List<MemberSeed> seeds = new ArrayList<>();
        for (int clubIndex = 0; clubIndex < CLUBS.size(); clubIndex++) {
            ClubSeed club = CLUBS.get(clubIndex);
            int memberCount = clubIndex % 2 == 0 ? 4 : 3;
            for (int memberIndex = 0; memberIndex < memberCount; memberIndex++) {
                seeds.add(createMemberSeed(club, clubIndex, memberIndex));
            }
        }
        return List.copyOf(seeds);
    }

    private static MemberSeed createMemberSeed(ClubSeed club, int clubIndex, int memberIndex) {
        String firstName = FIRST_NAMES.get((clubIndex + memberIndex) % FIRST_NAMES.size());
        String lastName = LAST_NAMES.get((clubIndex * 2 + memberIndex) % LAST_NAMES.size());
        OffsetDateTime memberBirthDate = birthDate(
                1985 + ((clubIndex + memberIndex) % 12),
                ((memberIndex * 3) % 12) + 1,
                ((clubIndex * 2 + memberIndex * 3) % 27) + 1
        );
        String emailLocalPart = (firstName + "." + lastName + (memberIndex + 1)).toLowerCase(Locale.ROOT);
        String emailDomain = club.city().toLowerCase(Locale.ROOT) + "-member.fr";
        String email = emailLocalPart + "@" + emailDomain;

        return new MemberSeed(
                UUID.nameUUIDFromBytes(
                        (club.id().toString() + ":member:" + memberIndex).getBytes(StandardCharsets.UTF_8)
                ),
                firstName,
                lastName,
                memberBirthDate,
                email,
                formatPhoneNumber(clubIndex, memberIndex),
                club.id(),
                memberIndex % 2 == 0,
                20 + (clubIndex * 8) + (memberIndex * 5)
        );
    }

    private static String formatPhoneNumber(int clubIndex, int memberIndex) {
        int seed = 10 + (clubIndex * 3 + memberIndex) % 90;
        return String.format(
                Locale.ROOT,
                "+33 6 %02d %02d %02d %02d",
                seed,
                rotateTwoDigits(seed, 13),
                rotateTwoDigits(seed, 26),
                rotateTwoDigits(seed, 39)
        );
    }

    private static int rotateTwoDigits(int seed, int offset) {
        return ((seed - 10 + offset) % 90) + 10;
    }

    private final ClubMemberCatalog clubMemberCatalog;

    public Fixture(ClubMemberCatalog clubMemberCatalog) {
        this.clubMemberCatalog = clubMemberCatalog;
    }

    @Override
    public void run(String... args) {
        MEMBERS.forEach(this::createIfMissing);
    }

    private void createIfMissing(MemberSeed seed) {
        if (clubMemberCatalog.findByClubIdAndUserId(seed.clubId(), seed.userId()) != null) {
            return;
        }

        ClubSeed club = CLUBS_BY_ID.get(seed.clubId());
        if (club == null) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        ClubMember member = new ClubMember();
        member.setClubId(club.id());
        member.setUserId(seed.userId());
        member.setFirstName(seed.firstName());
        member.setLastName(seed.lastName());
        member.setBirthDate(seed.birthDate());
        member.setEmail(seed.email());
        member.setPhoneNumber(seed.phoneNumber());
        member.setHimself(seed.himself());
        member.setIsActif(true);
        member.setDateSubscription(now.minusDays(seed.subscriptionDaysAgo()));

        member.setClubName(club.name());
        member.setClubAdresse(club.address());
        member.setClubCity(club.city());
        member.setClubOwnerId(ownerIdForCity(club.city()));
        member.setClubTypeId(club.sportId());
        member.setClubTypeName(SPORT_LABELS.getOrDefault(club.sportId(), "Sport"));
        member.setClubCountry(club.country());
        member.setClubPostalCode(club.postalCode());
        member.setClubPhoneNumber(club.phoneNumber());
        member.setClubEmail(club.email());
        member.setClubLatitude(club.latitude());
        member.setClubLongitude(club.longitude());
        member.setClubCreatedAt(now.minusDays(club.createdDaysAgo()));
        member.setClubUpdatedAt(now.minusDays(club.updatedDaysAgo()));
        member.setClubDeletedAt(null);
        member.setClubActive(true);

        clubMemberCatalog.save(member);
    }

    private static UUID ownerIdForCity(String city) {
        if (city == null) {
            return OWNER_PARIS;
        }
        return OWNER_BY_CITY.getOrDefault(city.toLowerCase(), OWNER_PARIS);
    }

    private static OffsetDateTime birthDate(int year, int month, int day) {
        return OffsetDateTime.of(year, month, day, 0, 0, 0, 0, ZoneOffset.UTC);
    }

    private record MemberSeed(
            UUID userId,
            String firstName,
            String lastName,
            OffsetDateTime birthDate,
            String email,
            String phoneNumber,
            UUID clubId,
            boolean himself,
            int subscriptionDaysAgo
    ) {
    }

    private record ClubSeed(
            UUID id,
            String name,
            UUID sportId,
            String city,
            String country,
            String address,
            String postalCode,
            String phoneNumber,
            String email,
            double latitude,
            double longitude,
            double rating,
            int memberCount,
            int createdDaysAgo,
            int updatedDaysAgo
    ) {
    }
}
