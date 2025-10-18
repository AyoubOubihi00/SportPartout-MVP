package fr.monclub.club_membership_service.Integration;

import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.service.ForcedUnSubscribeMemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ForcedUnSubscribeMemberServiceIntegrationTest {

    @Autowired
    private ForcedUnSubscribeMemberService forcedUnSubscribeMemberService;

    @Autowired
    private ClubMemberCatalog clubMemberCatalog;

    @Test
    void forcedUnsubscription_shouldReturnZero_ifMemberNotFound() {
        UUID clubId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        int result = forcedUnSubscribeMemberService.forcedUnsubscription(clubId, userId);

        assertThat(result).isEqualTo(0);
    }

    @Test
    void forcedUnsubscription_shouldDeactivateMember_andDeleteIt() {
        // Arrange : créer un membre dans la DB
        ClubMember member = new ClubMember();
        member.setClubId(UUID.randomUUID());
        member.setUserId(UUID.randomUUID());
        member.setIsActif(true);
        member.setHimself(true);
        member.setDateSubscription(LocalDate.now());
        clubMemberCatalog.save(member);

        // Act
        int result = forcedUnSubscribeMemberService.forcedUnsubscription(
                member.getClubId(), member.getUserId());

        // Assert
        assertThat(result).isEqualTo(1); // 1 ligne affectée
        ClubMember updatedMember = clubMemberCatalog.findByClubIdAndUserId(member.getClubId(), member.getUserId());
        assertThat(updatedMember).isNotNull();
        assertThat(updatedMember.getIsActif()).isFalse();
        assertThat(updatedMember.getHimself()).isFalse();
        assertThat(updatedMember.getDateSubscription()).isNull();
    }
}
