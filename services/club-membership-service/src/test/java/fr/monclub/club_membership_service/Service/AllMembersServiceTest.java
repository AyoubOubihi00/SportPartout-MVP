package fr.monclub.club_membership_service.Service;

import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;

import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.repository.ClubMemberRepository;
import fr.monclub.club_membership_service.service.AllMembersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AllMembersServiceTest {

    @Mock
    private ClubMemberCatalog clubMemberCatalog;

    @InjectMocks
    private AllMembersService allMembersService;

    private UUID clubId;
    private ClubMember activeMember;
    private ClubMember inactiveMember;

    @BeforeEach
    void setUp() {
        clubId = UUID.randomUUID();

        activeMember = new ClubMember();
        activeMember.setId(UUID.randomUUID());
        activeMember.setIsActif(true);
        activeMember.setEmail("active@example.com");
        activeMember.setClubId(clubId);

        inactiveMember = new ClubMember();
        inactiveMember.setId(UUID.randomUUID());
        inactiveMember.setIsActif(false);
        inactiveMember.setEmail("inactive@example.com");
        inactiveMember.setClubId(clubId);
    }

    @Test
    void getAllMembersIsActif_shouldReturnOnlyActiveMembers() {
        when(clubMemberCatalog.findAllByClubIdAndIsActifTrue(clubId))
                .thenReturn(List.of(activeMember));

        List<ClubMember> result = allMembersService.getAllMembersIsActif(clubId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActif()).isTrue();
        assertThat(result.get(0).getEmail()).isEqualTo("active@example.com");
    }

    @Test
    void getAllMembersNotActif_shouldReturnOnlyInactiveMembers() {
        when(clubMemberCatalog.findAllByClubIdAndIsActifFalse(clubId))
                .thenReturn(List.of(inactiveMember));

        List<ClubMember> result = allMembersService.getAllMembersNotActif(clubId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActif()).isFalse();
        assertThat(result.get(0).getEmail()).isEqualTo("inactive@example.com");
    }

    @Test
    void getMembersByClub_shouldReturnAllMembers() {
        when(clubMemberCatalog.findAllByClubId(clubId))
                .thenReturn(Arrays.asList(activeMember, inactiveMember));

        List<ClubMember> result = allMembersService.getMembersByClub(clubId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(activeMember, inactiveMember);
    }
}