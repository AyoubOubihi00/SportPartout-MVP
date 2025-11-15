package fr.monclub.club_membership_service.Integration;


import fr.monclub.club_membership_service.membership.model.ClubMember;
import fr.monclub.club_membership_service.membership.repository.ClubMemberRepository;
import fr.monclub.club_membership_service.membership.service.UnSunbscribeMemberService;
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
        ClubMember member = new ClubMember();
        member.setClubId(UUID.randomUUID());
        member.setUserId(UUID.randomUUID());
        member.setIsActif(true);
        clubMemberRepository.save(member);

        int result = unSunbscribeMemberService.unSubscribe(member.getClubId(), member.getUserId());

        assertThat(result).isEqualTo(1);

        ClubMember updatedMember = clubMemberRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getIsActif()).isFalse();
        assertThat(updatedMember.getDateSubscription()).isNull();
        assertThat(updatedMember.getHimself()).isTrue();
        assertThat(updatedMember.getDateUnsubscription()).isNotNull();
    }

    @Test
    void unSubscribe_shouldReturn0IfMemberNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        UUID unknownClubId = UUID.randomUUID();

        int result = unSunbscribeMemberService.unSubscribe(unknownClubId, unknownUserId);

        assertThat(result).isEqualTo(0);
    }
}
