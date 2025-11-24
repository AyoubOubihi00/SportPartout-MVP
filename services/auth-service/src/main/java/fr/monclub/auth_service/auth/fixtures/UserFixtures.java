package fr.monclub.auth_service.auth.fixtures;

import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class UserFixtures implements CommandLineRunner {

    @Value("${auth.fixture.default-password:ChangeMe#local}")
    private String defaultPassword;

    // Owners (gérants)
    public static final UUID OWNER_PARIS = UUID.fromString("3eb49b4e-762d-5022-b61c-c52c2dc414db");
    public static final UUID OWNER_LYON = UUID.fromString("d505d902-5519-53cf-8ab6-535b3c2e8d4b");
    public static final UUID OWNER_MARSEILLE = UUID.fromString("1e0cd442-27b2-5e5d-a7a1-e55a72c2b704");
    public static final UUID OWNER_METZ = UUID.fromString("8e71c78c-ec27-5dfe-bf57-308eb1878d6f");
    public static final UUID OWNER_NANCY = UUID.fromString("95fb5d0a-a8f3-573a-93b3-4b9eb6bae808");
    public static final UUID OWNER_STRASBOURG = UUID.fromString("5c69ecdd-85e9-5f65-9b1c-6487808ee449");

    // Comptes demo pour se connecter et voir des activités (role USER)
    public static final UUID USER_PARIS = UUID.fromString("11111111-2222-3333-4444-555555555555");
    public static final UUID USER_LYON = UUID.fromString("22222222-3333-4444-5555-666666666666");
    public static final UUID USER_METZ = UUID.fromString("33333333-4444-5555-6666-777777777777");
    public static final UUID USER_NANCY = UUID.fromString("44444444-5555-6666-7777-888888888888");
    public static final UUID USER_STRASBOURG = UUID.fromString("55555555-6666-7777-8888-999999999999");

    private static final List<UserSeed> USER_ACCOUNTS = List.of(
            new UserSeed(OWNER_PARIS, "Julien", "Leroy", "manager.paris@monclub.fr", "+33 6 45 23 99 10", birthDate(1985, 5, 12), "Gestionnaire des clubs parisiens", "OWNER"),
            new UserSeed(OWNER_LYON, "Emma", "Perrin", "manager.lyon@monclub.fr", "+33 6 12 94 33 01", birthDate(1989, 8, 3), "Gestionnaire des clubs lyonnais", "OWNER"),
            new UserSeed(OWNER_MARSEILLE, "Lucas", "Fabre", "manager.marseille@monclub.fr", "+33 6 20 55 10 30", birthDate(1987, 1, 27), "Gestionnaire des clubs marseillais", "OWNER"),
            new UserSeed(OWNER_METZ, "Camille", "Petit", "manager.metz@monclub.fr", "+33 6 78 44 10 20", birthDate(1986, 4, 16), "Gestionnaire des clubs messins", "OWNER"),
            new UserSeed(OWNER_NANCY, "Eva", "Michel", "manager.nancy@monclub.fr", "+33 6 65 20 13 42", birthDate(1990, 11, 9), "Gestionnaire des clubs nanciens", "OWNER"),
            new UserSeed(OWNER_STRASBOURG, "Antoine", "Vasseur", "manager.strasbourg@monclub.fr", "+33 6 75 40 80 32", birthDate(1983, 7, 14), "Gestionnaire des clubs strasbourgeois", "OWNER"),
            new UserSeed(USER_PARIS, "Pierre", "Durand", "pierre.demo@monclub.fr", "+33 6 11 22 33 44", birthDate(1992, 3, 10), "Utilisateur demo Paris", "USER"),
            new UserSeed(USER_LYON, "Paul", "Martin", "paul.demo@monclub.fr", "+33 6 22 33 44 55", birthDate(1991, 5, 8), "Utilisateur demo Lyon", "USER"),
            new UserSeed(USER_METZ, "Jacques", "Bernier", "jacques.demo@monclub.fr", "+33 6 33 44 55 66", birthDate(1993, 7, 2), "Utilisateur demo Metz", "USER"),
            new UserSeed(USER_NANCY, "Marie", "Lemoine", "marie.demo@monclub.fr", "+33 6 44 55 66 77", birthDate(1994, 9, 18), "Utilisateur demo Nancy", "USER"),
            new UserSeed(USER_STRASBOURG, "Sophie", "Weber", "sophie.demo@monclub.fr", "+33 6 55 66 77 88", birthDate(1990, 12, 4), "Utilisateur demo Strasbourg", "USER")
    );

    private final UserCatalog userCatalog;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        USER_ACCOUNTS.forEach(seed -> {
            boolean existsById = userCatalog.findById(seed.userId()).isPresent();
            boolean existsByEmail = userCatalog.findByEmail(seed.email()).isPresent();
            if (existsById || existsByEmail) {
                return;
            }
            userCatalog.save(seed.toUser(passwordEncoder, defaultPassword));
        });
    }

    private static OffsetDateTime birthDate(int year, int month, int day) {
        return OffsetDateTime.of(year, month, day, 0, 0, 0, 0, ZoneOffset.UTC);
    }

    private record UserSeed(
            UUID userId,
            String firstName,
            String lastName,
            String email,
            String phone,
            OffsetDateTime birthDate,
            String bio,
            String role
    ) {
        User toUser(PasswordEncoder encoder, String password) {
            return User.builder()
                    .id(userId)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(encoder.encode(password))
                    .role(role)
                    .phone(phone)
                    .bio(bio)
                    .profilePictureUrl(null)
                    .birthDate(birthDate)
                    .isActive(true)
                    .build();
        }
    }
}
