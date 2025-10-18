package fr.monclub.club_membership_service.Integration;

import fr.monclub.club_membership_service.DTO.UserInput;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.repository.ClubMemberRepository;
import fr.monclub.club_membership_service.service.SubscribeMemberService;
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
}
