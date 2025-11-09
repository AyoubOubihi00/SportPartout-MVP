package fr.monclub.club_membership_service.Integration;


import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import fr.monclub.club_membership_service.membership.service.AllMembersService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AllMembersServiceIntegrationTest {
    @Autowired

    private AllMembersService allMembersService;

    @Autowired
    private ClubMemberCatalog clubMemberCatalog;

    private UUID clubId;

    @BeforeEach
    void setUp() {
        clubId = UUID.randomUUID();

        // Création de quelques membres pour le test
        ClubMember member1 = new ClubMember();
        member1.setClubId(clubId);
        member1.setUserId(UUID.randomUUID());
        member1.setFirstName("Alice");
        member1.setLastName("Smith");
        member1.setEmail("alice@example.com");
       // member1.setBirthDate(OffsetDateTime.of(1990, 1, 1));
        member1.setIsActif(true);

        ClubMember member2 = new ClubMember();
        member2.setClubId(clubId);
        member2.setUserId(UUID.randomUUID());
        member2.setFirstName("Bob");
        member2.setLastName("Johnson");
        member2.setEmail("bob@example.com");
       // member2.setBirthDate(LocalDate.of(1985, 5, 5));
        member2.setIsActif(true);

        // Sauvegarde en base
        clubMemberCatalog.save(member1);
        clubMemberCatalog.save(member2);
    }

    @Test
    void getMembersByClub_shouldReturnAllMembersOfClub() {
        // Appel du service
        List<ClubMember> members = allMembersService.getMembersByClub(clubId);

        // Vérification
        assertThat(members).hasSize(2);
        assertThat(members).extracting("firstName").containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void getMembersByClub_shouldReturnEmptyList_whenNoMembers() {
        UUID unknownClubId = UUID.randomUUID();

        List<ClubMember> members = allMembersService.getMembersByClub(unknownClubId);

        assertThat(members).isEmpty();
    }
}
