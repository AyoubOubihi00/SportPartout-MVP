package fr.monclub.club_membership_service.fixtures;

import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.repository.ClubMemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@Profile("dev")
public class Fixture implements CommandLineRunner {
    private final ClubMemberCatalog clubMemberCatalog;


    public Fixture(ClubMemberCatalog clubMemberCatalog) {
        this.clubMemberCatalog = clubMemberCatalog;
    }


    UUID jeanUUID = UUID.fromString("0199bf12-d844-7f00-8c50-161091b014b1");
    UUID marieUUID = UUID.fromString("0199bf14-9b04-76ec-975c-43c86780dc2e");
    UUID paulUUID = UUID.fromString("0199bf14-fbf4-7215-beff-845d671e9953");
    UUID ahmetUUID = UUID.fromString("7d444840-9dc0-11d1-b245-5ffdce74fad2");

    UUID clubUUID = UUID.fromString("0199bf15-5e61-7eb0-82e1-4b972339c8d6");
    UUID club2UUID = UUID.fromString("2a121b9e-4c7a-49fe-9e96-4a3ff4356b6e");

    @Override
    public void run(String... args) {
        // Création des membres en base
        createIfNotExists(jeanUUID, "Jean", "Dupont", LocalDate.of(1990, 1, 1), "jean.dupont@email.com", clubUUID, "0601020304");
        createIfNotExists(marieUUID, "Marie", "Durand", LocalDate.of(1992, 2, 2), "marie.durand@email.com", clubUUID, "0605060708");
        createIfNotExists(paulUUID, "Paul", "Martin", LocalDate.of(1988, 3, 3), "paul.martin@email.com", clubUUID, "0608091011");
        createIfNotExists(ahmetUUID, "Ahmet", "Kalyoncu", LocalDate.of(1998, 10, 30), "ahmet@example.com", club2UUID, "0123456789");
        createIfNotExists(ahmetUUID, "Ahmet", "Kalyoncu", LocalDate.of(1998, 10, 30), "ahmet@example.com", clubUUID, "0123456789");

        System.out.println("Fixture GraphQL initialisée. Club id : " + clubUUID);
        System.out.println("Fixture GraphQL initialisée. Club2 id : " + club2UUID);

        // Exemples de requêtes GraphQL pour tester
    }

    private void createIfNotExists(UUID userId, String firstName, String lastName, LocalDate birthDate, String email, UUID clubUUID, String phoneNumber) {
        if (clubMemberCatalog.findByClubIdAndUserId(clubUUID, userId) != null) {
            return;
        }

        ClubMember member = new ClubMember();
        member.setClubId(clubUUID);
        member.setUserId(userId);
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setBirthDate(birthDate);
        member.setEmail(email);
        member.setPhoneNumber(phoneNumber);
        member.setIsActif(true);
        member.setHimself(true);
        member.setDateSubscription(LocalDate.now());

        clubMemberCatalog.save(member);
    }


}
