package fr.monclub.club_service.unit.services;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.mapper.UpdateClubInputToClubMapper;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.service.AddClubService;
import fr.monclub.club_service.club.service.DeleteClubService;
import fr.monclub.club_service.club.validators.UpdateClubValidator;
import fr.monclub.club_service.core.exception.ClubAccessDeniedException;
import fr.monclub.club_service.core.exception.ClubNotFoundException;
import fr.monclub.club_service.core.exception.ValidationException;
import fr.monclub.club_service.fixtures.ClubFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeleteClubServiceTest {

    @Mock
    private ClubCatalog clubCatalog;

    private DeleteClubService deleteClubService;

    @BeforeEach
    void setUp() {
        deleteClubService = new DeleteClubService(clubCatalog);
    }

    @Test
    void apply_shouldDeleteClub_whenOwnerMatches() {
        UUID ownerId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();

        Club existing = new Club();
        existing.setId(clubId);
        existing.setOwnerId(ownerId);
        existing.setActive(true);

        when(clubCatalog.getClubById(clubId)).thenReturn(existing);
        when(clubCatalog.save(any(Club.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = deleteClubService.apply(clubId, ownerId);

        assertThat(result).isTrue();

        verify(clubCatalog).getClubById(clubId);
        verify(clubCatalog).save(existing);
    }

    @Test
    void apply_shouldThrowException_whenOwnerDoesNotMatch() {
        UUID ownerId = UUID.randomUUID();
        UUID otherOwnerId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();

        Club existing = new Club();
        existing.setId(clubId);
        existing.setOwnerId(otherOwnerId);
        existing.setActive(true);

        when(clubCatalog.getClubById(clubId)).thenReturn(existing);

        assertThatThrownBy(() -> deleteClubService.apply(clubId, ownerId))
                .isInstanceOf(ClubAccessDeniedException.class)
                .hasMessageContaining("Vous n'êtes pas autorisé");

        verify(clubCatalog).getClubById(clubId);
        verify(clubCatalog, never()).save(any());
    }

    @Test
    void apply_shouldThrowException_whenClubNotFound() {
        UUID ownerId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();

        when(clubCatalog.getClubById(clubId)).thenReturn(null);

        assertThatThrownBy(() -> deleteClubService.apply(clubId, ownerId))
                .isInstanceOf(ClubNotFoundException.class);

        verify(clubCatalog).getClubById(clubId);
        verify(clubCatalog, never()).save(any());
    }

    @Test
    void apply_shouldNotReactivateClub_whenCalledTwice() {
        UUID ownerId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();

        Club existing = new Club();
        existing.setId(clubId);
        existing.setOwnerId(ownerId);
        existing.setActive(true);

        when(clubCatalog.getClubById(clubId)).thenReturn(existing);
        when(clubCatalog.save(any(Club.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Première suppression
        deleteClubService.apply(clubId, ownerId);

        assertThat(existing.isActive()).isFalse();

        // Deuxième suppression
        deleteClubService.apply(clubId, ownerId);

        assertThat(existing.isActive()).isFalse();
        assertThat(existing.getDeletedAt()).isNotNull();

        verify(clubCatalog, times(2)).getClubById(clubId);
        verify(clubCatalog, times(2)).save(existing);
    }






}
