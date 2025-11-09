package fr.monclub.club_membership_service.Service;


import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import fr.monclub.club_membership_service.membership.service.ForcedUnSubscribeMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ForcedUnSubscribeMemberServiceTest {

    @Mock
    private ClubMemberCatalog clubMemberCatalog;

    @InjectMocks
    private ForcedUnSubscribeMemberService service;

    private UUID clubId;
    private UUID userId;
    private ClubMember clubMember;

    @BeforeEach
    void setUp() {
        clubId = UUID.randomUUID();
        userId = UUID.randomUUID();

        // Création d'un ClubMember fictif
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

        // Mock du comportement du catalogue
        when(clubMemberCatalog.findByClubIdAndUserId(clubId, userId))
                .thenReturn(clubMember);
        when(clubMemberCatalog.deactivateClubMemberById(clubMember.getId()))
                .thenReturn(1); // simulate update: 1 ligne affectée
    }

    @Test
    void forcedUnsubscription_shouldDeactivateAndDeleteMember() {
        // Appel de la méthode à tester
        int result = service.forcedUnsubscription(clubId, userId);

        // Vérifications sur le résultat
        assertThat(result).isEqualTo(1);
        assertThat(clubMember.getIsActif()).isFalse();
        assertThat(clubMember.getDateSubscription()).isNull();
        assertThat(clubMember.getHimself()).isFalse();

        // Vérifier les interactions avec le catalogue
        verify(clubMemberCatalog).findByClubIdAndUserId(clubId, userId);
        verify(clubMemberCatalog).deactivateClubMemberById(clubMember.getId());
    }

    @Test
    void forcedUnsubscription_shouldReturnZeroIfMemberNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        when(clubMemberCatalog.findByClubIdAndUserId(clubId, unknownUserId)).thenReturn(null);

        int result = service.forcedUnsubscription(clubId, unknownUserId);

        assertThat(result).isEqualTo(0);
        verify(clubMemberCatalog).findByClubIdAndUserId(clubId, unknownUserId);
        verify(clubMemberCatalog, never()).deactivateClubMemberById(any());
    }
}
