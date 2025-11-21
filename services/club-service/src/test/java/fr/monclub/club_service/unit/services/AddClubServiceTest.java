package fr.monclub.club_service.unit.services;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.mapper.UpdateClubInputToClubMapper;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.service.AddClubService;
import fr.monclub.club_service.club.validators.UpdateClubValidator;
import fr.monclub.club_service.config.RabbitMQ.Publisher.ClubEventPublisher;
import fr.monclub.club_service.core.exception.ValidationException;
import fr.monclub.club_service.fixtures.ClubFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddClubServiceTest {

    @Mock
    private ClubCatalog clubCatalog;

    @Autowired
    private UpdateClubInputToClubMapper mapper;

    private AddClubService addClubService;

    @Autowired
    private UpdateClubValidator validator;

    @Mock
    private ClubEventPublisher clubEventPublisher;

    @BeforeEach
    void setUp() {
        addClubService = new AddClubService(clubCatalog, mapper, validator, clubEventPublisher);
    }

    @Test
    void apply_shouldUpdateClub_whenOwnerMatches() {
        UUID ownerId = UUID.randomUUID();

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

        Club mapped = new Club();
        mapped.setId(UUID.randomUUID());
        mapped.setName("New Name");
        mapped.setCity("Paris");
        mapped.setTypeName("tennis");

        when(clubCatalog.save(any(Club.class))).thenReturn(mapped);
        when(clubCatalog.getClubById(mapped.getId())).thenReturn(mapped);


        Club updated = addClubService.apply(input, ownerId);
        Club res = clubCatalog.getClubById(updated.getId());

        assertEquals("New Name", res.getName());
        assertEquals("tennis", res.getTypeName());
        assertEquals("Paris", res.getCity());
    }

    @Test
    void apply_shouldThrowValidationException_whenInputIsNull() {
        UUID clubId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        Club existingClub = new Club();
        existingClub.setId(clubId);
        existingClub.setOwnerId(ownerId);

        when(clubCatalog.getClubById(clubId)).thenReturn(existingClub);

        assertThrows(ValidationException.class,
                () -> addClubService.apply(null, ownerId));

        verify(clubCatalog, never()).save(any());
    }

    @Test
    void apply_shouldUpdateClubPartially_whenSomeFieldsAreNull() {
        UUID ownerId = UUID.randomUUID();

        UpdateClubInput input = new UpdateClubInput();
        input.setName(null);
        input.setTypeId(ClubFixtures.UUID_FOOTBALL);
        input.setTypeName("football");
        input.setCity(null);

        assertThrows(ValidationException.class,
                () -> addClubService.apply(input, ownerId));

        verify(clubCatalog, never()).save(any());
    }
}
