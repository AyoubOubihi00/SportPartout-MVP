package fr.monclub.club_membership_service.Service;

import fr.monclub.club_membership_service.membership.DTO.ClubInput;
import fr.monclub.club_membership_service.membership.DTO.UserInput;
import fr.monclub.club_membership_service.membership.Mapper.ClubMemberMapper;
import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import fr.monclub.club_membership_service.membership.service.SubscribeMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscribeServiceTest {

    @Mock
    ClubMemberCatalog clubMemberCatalog;

    @Mock
    ClubMemberMapper clubMemberMapper;

    @InjectMocks
    SubscribeMemberService service;

    private UUID clubId;
    private UserInput userInput;
    private ClubMember clubMember;

    @BeforeEach
    void setUp() {
        clubId = UUID.randomUUID();
        userInput = new UserInput();
        userInput.setUserId(UUID.randomUUID());
        userInput.setFirstName("Jane");
        userInput.setLastName("Doe");
        userInput.setEmail("jane@example.com");
        clubMember = new ClubMember();
    }

    @Test
    void subscribe_shouldSaveMemberAndReturnEntity() {
        when(clubMemberCatalog.findByClubIdAndUserId(clubId, userInput.getUserId())).thenReturn(null);
        when(clubMemberMapper.toClubMemberEntity(eq(clubId), eq(userInput), any(ClubInput.class))).thenReturn(clubMember);
        when(clubMemberCatalog.save(clubMember)).thenReturn(clubMember);

        ClubMember result = service.subscribe(clubId, userInput);

        assertThat(result).isEqualTo(clubMember);
        verify(clubMemberCatalog).save(clubMember);
        verify(clubMemberMapper).toClubMemberEntity(eq(clubId), eq(userInput), any(ClubInput.class));
    }

    @Test
    void subscribe_shouldReturnNull_whenMemberAlreadyActive() {
        ClubMember active = new ClubMember();
        active.setIsActif(true);
        when(clubMemberCatalog.findByClubIdAndUserId(clubId, userInput.getUserId())).thenReturn(active);

        ClubMember result = service.subscribe(clubId, userInput);

        assertThat(result).isNull();
        verify(clubMemberCatalog, never()).save(any());
        verifyNoInteractions(clubMemberMapper);
    }

    @Test
    void subscribe_shouldReactivateInactiveMember() {
        ClubMember inactive = new ClubMember();
        inactive.setIsActif(false);
        when(clubMemberCatalog.findByClubIdAndUserId(clubId, userInput.getUserId())).thenReturn(inactive);
        when(clubMemberCatalog.save(inactive)).thenReturn(inactive);

        ClubInput clubInput = new ClubInput();
        clubInput.setName("Club Reactivation");

        ClubMember result = service.subscribe(clubId, userInput, clubInput);

        assertThat(result).isSameAs(inactive);
        assertThat(inactive.getIsActif()).isTrue();
        assertThat(inactive.getDateSubscription()).isNotNull();
        assertThat(inactive.getDateUnsubscription()).isNull();
        verify(clubMemberCatalog).save(inactive);
        verify(clubMemberMapper, never()).toClubMemberEntity(eq(clubId), eq(userInput), eq(clubInput));
    }
}
