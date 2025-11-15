package fr.monclub.club_membership_service.Service;

import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import fr.monclub.club_membership_service.membership.service.AllClubMembersByUserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AllClubMembersByUserIdTest {

    @Mock
    private ClubMemberCatalog clubMemberCatalog;

    @InjectMocks
    private AllClubMembersByUserId service;

    private UUID userId;
    private UUID club1;
    private UUID club2;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        club1 = UUID.randomUUID();
        club2 = UUID.randomUUID();
    }

    @Test
    void findClubIdsByUserId_shouldReturnOnlyActiveClubMembers() {
        ClubMember m1 = new ClubMember();
        m1.setClubId(club1);
        m1.setIsActif(true);

        ClubMember m2 = new ClubMember();
        m2.setClubId(club2);
        m2.setIsActif(true);

        ClubMember inactive = new ClubMember();
        inactive.setClubId(UUID.randomUUID());
        inactive.setIsActif(false);

        List<ClubMember> memberships = Arrays.asList(m1, m2, inactive);
        when(clubMemberCatalog.findClubMembersByUserId(userId)).thenReturn(memberships);

        List<ClubMember> result = service.findClubIdsByUserId(userId);

        List<UUID> ids = result.stream().map(ClubMember::getClubId).collect(Collectors.toList());
        assertThat(ids).isEqualTo(Arrays.asList(club1, club2));
    }
}
