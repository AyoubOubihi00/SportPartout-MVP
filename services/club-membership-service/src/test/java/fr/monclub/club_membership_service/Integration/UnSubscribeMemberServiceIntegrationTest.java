package fr.monclub.club_membership_service.Integration;

import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.repository.ClubMemberRepository;
import fr.monclub.club_membership_service.service.UnSunbscribeMemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UnSubscribeMemberServiceIntegrationTest {

    @Autowired
    private UnSunbscribeMemberService unSunbscribeMemberService;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Test
    void unSubscribe_shouldDeactivateMember() {
        // Arrange : créer un membre dans la DB
        ClubMember member = new ClubMember();
        member.setClubId(UUID.randomUUID());
        member.setUserId(UUID.randomUUID());
        member.setIsActif(true);
        clubMemberRepository.save(member);

        // Act
        int result = unSunbscribeMemberService.unSubscribe(member.getClubId(), member.getUserId());

        // Assert
        assertThat(result).isEqualTo(1);  // 1 = désactivation réussie

        // Récupérer le membre depuis la DB
        ClubMember updatedMember = clubMemberRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getIsActif()).isFalse();  // Vérifie qu'il est désactivé
        assertThat(updatedMember.getDateSubscription()).isNull(); // Vérifie que la date de souscription est null
    }

    @Test
    void unSubscribe_shouldReturn0IfMemberNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        UUID unknownClubId = UUID.randomUUID();

        int result = unSunbscribeMemberService.unSubscribe(unknownClubId, unknownUserId);

        assertThat(result).isEqualTo(0);  // 0 = membre introuvable
    }
}
