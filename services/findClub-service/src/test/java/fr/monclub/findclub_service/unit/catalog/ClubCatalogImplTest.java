package fr.monclub.findclub_service.unit.catalog;

import fr.monclub.findclub_service.club.catalog.FindClubCatalogImpl;
import fr.monclub.findclub_service.club.model.FindClubWithDistance;
import fr.monclub.findclub_service.club.model.Club;
import fr.monclub.findclub_service.club.repository.FindClubRepository;
import fr.monclub.findclub_service.fixtures.ClubFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClubCatalogImplTest {

    @Mock
    private FindClubRepository findClubRepository;

    @InjectMocks
    private FindClubCatalogImpl clubCatalog;

    private Club parisClub;
    private Club lyonClub;

    @BeforeEach
    void setUp() {
        parisClub = buildClub(
                UUID.randomUUID(),
                "Paris Running",
                "Paris",
                "75000",
                48.8566,
                2.3522
        );

        lyonClub = buildClub(
                UUID.randomUUID(),
                "Lyon Basket",
                "Lyon",
                "69000",
                45.7640,
                4.8357
        );
    }

    @Test
    void getClubsByCity_shouldDelegateToRepositoryWithNormalizedCity() {
        when(findClubRepository.findAllByCityAndPostalCode("paris", null))
                .thenReturn(List.of(parisClub));

        List<Club> result = clubCatalog.getClubsByCity("  paris ");

        assertThat(result).containsExactly(parisClub);
    }

    @Test
    void getClubsByCityAndPostalCode_shouldForwardBothParameters() {
        when(findClubRepository.findAllByCityAndPostalCode("paris", "75000"))
                .thenReturn(List.of(parisClub));

        List<Club> result = clubCatalog.getClubsByCityAndPostalCode("Paris", "75000");

        assertThat(result).containsExactly(parisClub);
    }

    @Test
    void getClubsByCityAndPostalCode_shouldFallbackToCityWhenPostalDoesNotMatch() {
        when(findClubRepository.findAllByCityAndPostalCode("paris", "75000"))
                .thenReturn(List.of());
        when(findClubRepository.findAllByCityAndPostalCode("paris", null))
                .thenReturn(List.of(parisClub));

        List<Club> result = clubCatalog.getClubsByCityAndPostalCode("Paris", "75000");

        assertThat(result).containsExactly(parisClub);
    }

    @Test
    void getClubsNear_shouldReturnRepositoryResult() {
        List<FindClubWithDistance> expected = List.of(
                new FindClubWithDistance(parisClub, 0),
                new FindClubWithDistance(lyonClub, 390)
        );
        when(findClubRepository.findWithinRadius(48.8566, 2.3522, 10))
                .thenReturn(expected);

        List<FindClubWithDistance> result = clubCatalog.getClubsNear(48.8566, 2.3522, 10);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getClubsNear_withNegativeRadius_shouldClampToZero() {
        when(findClubRepository.findWithinRadius(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of());

        clubCatalog.getClubsNear(48.8566, 2.3522, -5);

        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        verify(findClubRepository).findWithinRadius(eq(48.8566), eq(2.3522), captor.capture());
        assertThat(captor.getValue()).isZero();
    }

    private Club buildClub(UUID id, String name, String city, String postalCode, double latitude, double longitude) {
        Club club = new Club();
        club.setId(id);
        club.setName(name);
        club.setTypeIdSport(ClubFixtures.UUID_FOOTBALL);
        club.setTypeNameSport("football");
        club.setCity(city);
        club.setPostalCode(postalCode);
        club.setCountry("France");
        club.setAddress("1 rue de Test");
        club.setLatitude(latitude);
        club.setLongitude(longitude);
        return club;
    }
}