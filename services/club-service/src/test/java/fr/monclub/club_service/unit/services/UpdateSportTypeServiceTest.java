package fr.monclub.club_service.unit.services;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.mapper.UpdateClubInputToClubMapper;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.service.UpdateClubService;
import fr.monclub.club_service.club.service.UpdateSportTypeService;
import fr.monclub.club_service.club.validators.UpdateClubValidator;
import fr.monclub.club_service.core.exception.ClubAccessDeniedException;
import fr.monclub.club_service.core.exception.ValidationException;
import fr.monclub.club_service.fixtures.ClubFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class UpdateSportTypeServiceTest {

    @Mock
    private ClubCatalog clubCatalog;


    private UpdateSportTypeService updateSportTypeService;

    @Autowired
    private UpdateClubValidator validator;

    @BeforeEach
    void setUp() {
        updateSportTypeService = new UpdateSportTypeService(clubCatalog);
    }

    @Test
    void apply_shouldUpdateTypeNameOfAllClubsWithGivenSportTypeId() {
        UUID sportTypeId = UUID.randomUUID();

        Club club1 = new Club();
        club1.setId(UUID.randomUUID());
        club1.setTypeId(sportTypeId);
        club1.setTypeName("football");

        Club club2 = new Club();
        club2.setId(UUID.randomUUID());
        club2.setTypeId(sportTypeId);
        club2.setTypeName("football");

        List<Club> clubs = List.of(club1, club2);

        when(clubCatalog.getAllClubsBySportTypeId(sportTypeId)).thenReturn(clubs);

        updateSportTypeService.apply(sportTypeId, "football 2");

        assertThat(club1.getTypeName()).isEqualTo("football 2");
        assertThat(club2.getTypeName()).isEqualTo("football 2");

        verify(clubCatalog).saveAll(clubs);
    }

    @Test
    void apply_shouldHandleNoClubsFound() {
        UUID sportTypeId = UUID.randomUUID();

        when(clubCatalog.getAllClubsBySportTypeId(sportTypeId)).thenReturn(List.of());

        updateSportTypeService.apply(sportTypeId, "new name");

        verify(clubCatalog, never()).saveAll(anyList());
    }

}
