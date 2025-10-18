package fr.monclub.club_membership_service.Service;

import fr.monclub.club_membership_service.DTO.UserInput;
import fr.monclub.club_membership_service.Mapper.ClubMemberMapper;
import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;

import fr.monclub.club_membership_service.DTO.UserInput;
import fr.monclub.club_membership_service.Mapper.ClubMemberMapper;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.repository.ClubMemberRepository;
import fr.monclub.club_membership_service.service.SubscribeMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        clubMember = new ClubMember();
    }




    @Test
    void subscribe_shouldSaveMemberAndReturnEntity() {
        // Arrange : on définit le comportement des mocks
        when(clubMemberMapper.toClubMemberEntity(clubId, userInput)).thenReturn(clubMember);

        when(clubMemberCatalog.save(clubMember)).thenReturn(clubMember);

        // Act : on appelle la vraie méthode du service
        ClubMember result = service.subscribe(clubId, userInput);

        // Assert : on vérifie le résultat et l’interaction avec les mocks
        assertThat(result).isEqualTo(clubMember);
        verify(clubMemberCatalog).save(clubMember);
        verify(clubMemberMapper).toClubMemberEntity(clubId, userInput);
    }
}
