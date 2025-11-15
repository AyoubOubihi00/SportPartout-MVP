package fr.monclub.club_membership_service.Service;


import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import fr.monclub.club_membership_service.membership.service.UnSunbscribeMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnSunbscribeMemberServiceTest {

    @Mock
    private ClubMemberCatalog clubMemberCatalog;

    @InjectMocks
    private UnSunbscribeMemberService service;

    private UUID clubId;
    private UUID userId;
    private ClubMember clubMember;

    @BeforeEach
    void setUp() {
        clubId = UUID.randomUUID();
        userId = UUID.randomUUID();

        // Cr?ation d'un ClubMember simul?
        clubMember = new ClubMember();
        clubMember.setId(UUID.randomUUID());
        clubMember.setIsActif(true);
        clubMember.setEmail("ahmet.kaly@gmail.com");
        clubMember.setFirstName("Ahmet");
        clubMember.setLastName("Kaly");
        clubMember.setPhoneNumber("0606060606");
        clubMember.setBirthDate(OffsetDateTime.now());
        clubMember.setClubId(clubId);
        clubMember.setUserId(userId);
        clubMember.setDateSubscription(OffsetDateTime.now());
    }

    @Test
    void unsubscribe_shouldDeactivateClubMemberAndReturn1() {
        when(clubMemberCatalog.findByClubIdAndUserId(clubId, userId))
                .thenReturn(clubMember);
        when(clubMemberCatalog.deactivateClubMemberById(clubMember.getId()))
                .thenReturn(1); // simulate successful update
        // Appel de la méthode à tester
        int result = service.unSubscribe(clubId, userId);

        // Assertions sur le résultat
        assertThat(result).isEqualTo(1);
        assertThat(clubMember.getIsActif()).isFalse();
        assertThat(clubMember.getDateSubscription()).isNull();

        // Vérifier que les interactions avec le catalogue ont eu lieu
        verify(clubMemberCatalog).findByClubIdAndUserId(clubId, userId);
        verify(clubMemberCatalog).deactivateClubMemberById(clubMember.getId());
    }

    @Test
    void unsubscribe_shouldReturn0IfMemberNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        when(clubMemberCatalog.findByClubIdAndUserId(clubId, unknownUserId)).thenReturn(null);

        int result = service.unSubscribe(clubId, unknownUserId);

        assertThat(result).isEqualTo(0);
        verify(clubMemberCatalog).findByClubIdAndUserId(clubId, unknownUserId);
        verify(clubMemberCatalog, never()).save(any());
    }
}
