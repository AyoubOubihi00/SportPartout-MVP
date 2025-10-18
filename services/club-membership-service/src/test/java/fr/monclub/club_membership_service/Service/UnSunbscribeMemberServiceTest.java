package fr.monclub.club_membership_service.Service;

import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.service.UnSunbscribeMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

        // Création d'un ClubMember simulé
        clubMember = new ClubMember();
        clubMember.setId(UUID.randomUUID());
        clubMember.setIsActif(true);
        clubMember.setEmail("ahmet.kaly@gmail.com");
        clubMember.setFirstName("Ahmet");
        clubMember.setLastName("Kaly");
        clubMember.setPhoneNumber("0606060606");
        clubMember.setBirthDate(LocalDate.now());
        clubMember.setClubId(clubId);
        clubMember.setUserId(userId);
        clubMember.setDateSubscription(LocalDate.now());


    }

    @Test
    void unsubscribe_shouldDeactivateClubMemberAndReturn1() {
        // Mock du comportement du catalogue
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
        // Mock du comportement du catalogue

        UUID unknownUserId = UUID.randomUUID();
        when(clubMemberCatalog.findByClubIdAndUserId(clubId, unknownUserId)).thenReturn(null);

        int result = service.unSubscribe(clubId, unknownUserId);

        assertThat(result).isEqualTo(0);
        verify(clubMemberCatalog).findByClubIdAndUserId(clubId, unknownUserId);
        verify(clubMemberCatalog, never()).deactivateClubMemberById(any());
    }
}
