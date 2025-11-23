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

    public static final UUID OWNER_PARIS = UUID.fromString("3eb49b4e-762d-5022-b61c-c52c2dc414db");
    public static final UUID OWNER_LYON = UUID.fromString("d505d902-5519-53cf-8ab6-535b3c2e8d4b");
    public static final UUID OWNER_MARSEILLE = UUID.fromString("1e0cd442-27b2-5e5d-a7a1-e55a72c2b704");
    public static final UUID OWNER_METZ = UUID.fromString("8e71c78c-ec27-5dfe-bf57-308eb1878d6f");
    public static final UUID OWNER_NANCY = UUID.fromString("95fb5d0a-a8f3-573a-93b3-4b9eb6bae808");
    public static final UUID OWNER_STRASBOURG = UUID.fromString("5c69ecdd-85e9-5f65-9b1c-6487808ee449");

    private static final List<OwnerSeed> OWNER_ACCOUNTS = List.of(
            new OwnerSeed(OWNER_PARIS, "Julien", "Leroy", "manager.paris@monclub.fr", "+33 6 45 23 99 10", birthDate(1985, 5, 12), "Gestionnaire des clubs parisiens"),
            new OwnerSeed(OWNER_LYON, "Emma", "Perrin", "manager.lyon@monclub.fr", "+33 6 12 94 33 01", birthDate(1989, 8, 3), "Gestionnaire des clubs lyonnais"),
            new OwnerSeed(OWNER_MARSEILLE, "Lucas", "Fabre", "manager.marseille@monclub.fr", "+33 6 20 55 10 30", birthDate(1987, 1, 27), "Gestionnaire des clubs marseillais"),
            new OwnerSeed(OWNER_METZ, "Camille", "Petit", "manager.metz@monclub.fr", "+33 6 78 44 10 20", birthDate(1986, 4, 16), "Gestionnaire des clubs messins"),
            new OwnerSeed(OWNER_NANCY, "Eva", "Michel", "manager.nancy@monclub.fr", "+33 6 65 20 13 42", birthDate(1990, 11, 9), "Gestionnaire des clubs nancéiens"),
            new OwnerSeed(OWNER_STRASBOURG, "Antoine", "Vasseur", "manager.strasbourg@monclub.fr", "+33 6 75 40 80 32", birthDate(1983, 7, 14), "Gestionnaire des clubs strasbourgeois")
    );

    private final UserCatalog userCatalog;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        OWNER_ACCOUNTS.forEach(seed -> userCatalog.findByEmail(seed.email())
                .orElseGet(() -> userCatalog.save(seed.toUser(passwordEncoder, defaultPassword))));
    }

    private static OffsetDateTime birthDate(int year, int month, int day) {
        return OffsetDateTime.of(year, month, day, 0, 0, 0, 0, ZoneOffset.UTC);
    }

    private record OwnerSeed(
            UUID ownerId,
            String firstName,
            String lastName,
            String email,
            String phone,
            OffsetDateTime birthDate,
            String bio
    ) {
        User toUser(PasswordEncoder encoder, String password) {
            return User.builder()
                    .id(ownerId)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(encoder.encode(password))
                    .role("OWNER")
                    .phone(phone)
                    .bio(bio)
                    .profilePictureUrl(null)
                    .birthDate(birthDate)
                    .isActive(true)
                    .build();
        }
    }
}
