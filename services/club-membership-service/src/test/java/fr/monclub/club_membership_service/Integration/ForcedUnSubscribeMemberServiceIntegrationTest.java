package fr.monclub.club_membership_service.Integration;


import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import fr.monclub.club_membership_service.membership.service.ForcedUnSubscribeMemberService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
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
    void forcedUnsubscription_shouldDeactivateMember_andKeepHistory() {
        ClubMember member = new ClubMember();
        member.setClubId(UUID.randomUUID());
        member.setUserId(UUID.randomUUID());
        member.setIsActif(true);
        member.setHimself(true);
        member.setDateSubscription(OffsetDateTime.now());
        clubMemberCatalog.save(member);

        int result = forcedUnSubscribeMemberService.forcedUnsubscription(
                member.getClubId(), member.getUserId());

        assertThat(result).isEqualTo(1);
        ClubMember updatedMember = clubMemberCatalog.findByClubIdAndUserId(member.getClubId(), member.getUserId());
        assertThat(updatedMember).isNotNull();
        assertThat(updatedMember.getIsActif()).isFalse();
        assertThat(updatedMember.getHimself()).isFalse();
        assertThat(updatedMember.getDateSubscription()).isNull();
        assertThat(updatedMember.getDateUnsubscription()).isNotNull();
    }
}
