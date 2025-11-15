package fr.monclub.club_service.unit.services;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.config.RabbitMQ.Publisher.ClubEventPublisher;
import fr.monclub.club_service.core.exception.ClubAccessDeniedException;
import fr.monclub.club_service.core.exception.ValidationException;
import fr.monclub.club_service.club.mapper.UpdateClubInputToClubMapper;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.service.UpdateClubService;
import fr.monclub.club_service.club.validators.UpdateClubValidator;
import fr.monclub.club_service.fixtures.ClubFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class UpdateClubServiceTest {

    @Mock
    private ClubCatalog clubCatalog;

    @Autowired
    private UpdateClubInputToClubMapper mapper;

    private UpdateClubService updateClubService;

    @Mock
    private ClubEventPublisher clubEventPublisher;

    @Autowired
    private UpdateClubValidator validator;

    @BeforeEach
    void setUp() {
        updateClubService = new UpdateClubService(clubCatalog, mapper, validator,clubEventPublisher);
    }

    @Test
    void apply_shouldUpdateClub_whenOwnerMatches() {
        UUID clubId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        Club existingClub = new Club();
        existingClub.setId(clubId);
        existingClub.setOwnerId(ownerId);
        existingClub.setName("Old Name");
        existingClub.setTypeId(ClubFixtures.UUID_FOOTBALL);
        existingClub.setTypeName("football");

        UpdateClubInput input = new UpdateClubInput();
        input.setName("New Name");
        input.setCity("Paris");
        input.setAddress("10 Rue Exemple");
        input.setCountry("France");
        input.setPhoneNumber("0123456789");
        input.setEmail("contact@club.com");
        input.setLatitude(48.8566);
        input.setLongitude(2.3522);
        input.setTypeId(ClubFixtures.UUID_TENNIS);
        input.setTypeName("tennis");

        when(clubCatalog.getClubById(clubId)).thenReturn(existingClub);
        when(clubCatalog.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Club updated = updateClubService.apply(clubId, input, ownerId);

        assertEquals("New Name", updated.getName());
        assertEquals("tennis", updated.getTypeName());
        assertEquals("Paris", updated.getCity());

        Club res = clubCatalog.getClubById(clubId);
        assertEquals("New Name", res.getName());
        assertEquals("tennis", res.getTypeName());
        assertEquals("Paris", res.getCity());
    }

    @Test
    void apply_shouldThrowException_whenOwnerDoesNotMatch() {
        UUID clubId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID wrongOwnerId = UUID.randomUUID();

        Club existingClub = new Club();
        existingClub.setId(clubId);
        existingClub.setOwnerId(ownerId);

        UpdateClubInput input = new UpdateClubInput();

        when(clubCatalog.getClubById(clubId)).thenReturn(existingClub);

        ClubAccessDeniedException ex = assertThrows(
                ClubAccessDeniedException.class,
                () -> updateClubService.apply(clubId, input, wrongOwnerId)
        );

        assertEquals("Vous n'êtes pas autorisé à modifier ce club.", ex.getMessage());
        verify(clubCatalog, never()).save(any());
    }

    @Test
    void apply_shouldThrowException_whenClubNotFound() {
        UUID clubId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        UpdateClubInput input = new UpdateClubInput();

        when(clubCatalog.getClubById(clubId)).thenThrow(new RuntimeException("Club not found"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> updateClubService.apply(clubId, input, ownerId)
        );

        assertEquals("Club not found", ex.getMessage());
        verify(clubCatalog, never()).save(any());
    }

}
