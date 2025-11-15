package fr.monclub.club_membership_service.Integration;

import fr.monclub.club_membership_service.membership.DTO.UserInput;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import fr.monclub.club_membership_service.membership.repository.ClubMemberRepository;
import fr.monclub.club_membership_service.membership.service.SubscribeMemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class SubscribeMemberServiceIntegrationTest {

    @Autowired
    private SubscribeMemberService subscribeMemberService;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Test
    void subscribe_shouldPersistMember() {
        // Arrange
        UUID clubId = UUID.randomUUID();
        UserInput userInput = new UserInput();
        userInput.setUserId(UUID.randomUUID());
        userInput.setFirstName("John");
        userInput.setLastName("Doe");
        userInput.setEmail("john@example.com");

        // Act
        ClubMember result = subscribeMemberService.subscribe(clubId, userInput);

        // Assert : le membre est bien enregistré en DB
        ClubMember saved = clubMemberRepository.findById(result.getId()).orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
    }

    @Test
    void subscribe_shouldReactivateInactiveMember() {
        UUID clubId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserInput userInput = new UserInput();
        userInput.setUserId(userId);
        userInput.setFirstName("Jane");
        userInput.setLastName("Doe");
        userInput.setEmail("jane@example.com");

        ClubMember existing = new ClubMember();
        existing.setClubId(clubId);
        existing.setUserId(userId);
        existing.setIsActif(false);
        existing.setFirstName("Old");
        existing.setEmail("old@example.com");
        clubMemberRepository.save(existing);

        ClubMember result = subscribeMemberService.subscribe(clubId, userInput);

        assertThat(result).isNotNull();
        assertThat(result.getIsActif()).isTrue();
        assertThat(result.getDateSubscription()).isNotNull();
        assertThat(result.getDateUnsubscription()).isNull();
        ClubMember reloaded = clubMemberRepository.findById(result.getId()).orElseThrow();
        assertThat(reloaded.getEmail()).isEqualTo("jane@example.com");
    }
}
