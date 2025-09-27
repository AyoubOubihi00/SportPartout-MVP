package fr.monclub.findclub_service.integration;

import fr.monclub.findclub_service.club.catalog.FindClubCatalog;
import fr.monclub.findclub_service.club.model.FindClubWithDistance;
import fr.monclub.findclub_service.club.controller.FindClubController;
import fr.monclub.findclub_service.club.model.Club;
import fr.monclub.findclub_service.config.GraphQLConfig;
import fr.monclub.findclub_service.fixtures.ClubFixtures;
import fr.monclub.findclub_service.location.LocationService;
import fr.monclub.findclub_service.location.LocationSuggestion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@GraphQlTest(FindClubController.class)
@Import(GraphQLConfig.class)
class ClubControllerIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private FindClubCatalog clubCatalog;

    @MockitoBean
    private LocationService locationService;

    @Test
    void shouldReturnAllClubs() {
        Club c1 = new Club();
        c1.setId(UUID.randomUUID());
        c1.setName("Club1");

        Club c2 = new Club();
        c2.setId(UUID.randomUUID());
        c2.setName("Club2");

        when(clubCatalog.getAllClubs()).thenReturn(List.of(c1, c2));

        String query = """
                query {
                  clubs {
                    name
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("clubs[*].name")
                .entityList(String.class)
                .containsExactly("Club1", "Club2");
    }

    @Test
    void shouldReturnClubsByCity() {
        Club parisClub = new Club();
        parisClub.setId(UUID.randomUUID());
        parisClub.setName("Paris Club");
        parisClub.setCity("Paris");

        when(clubCatalog.getClubsByCityAndPostalCode("Paris", null)).thenReturn(List.of(parisClub));

        String query = """
            query($city: String!) {
              clubsByCity(city: $city) {
                name
              }
            }
            """;

        graphQlTester.document(query)
                .variable("city", "Paris")
                .execute()
                .path("clubsByCity[*].name")
                .entityList(String.class)
                .containsExactly("Paris Club");
    }

    @Test
    void shouldReturnClubsNear() {
        Club club = new Club();
        club.setId(UUID.randomUUID());
        club.setName("Nearby Club");
        club.setLatitude(48.8566);
        club.setLongitude(2.3522);
        club.setTypeIdSport(ClubFixtures.UUID_FOOTBALL);
        club.setTypeNameSport("football");

        FindClubWithDistance nearby = new FindClubWithDistance(club, 2.5);

        when(clubCatalog.getClubsNear(48.8566, 2.3522, 10.0)).thenReturn(List.of(nearby));

        String query = """
            query($lat: Float!, $lon: Float!, $radius: Float!) {
              clubsNear(latitude: $lat, longitude: $lon, radiusKm: $radius) {
                name
                distanceKm
              }
            }
            """;

        graphQlTester.document(query)
                .variable("lat", 48.8566)
                .variable("lon", 2.3522)
                .variable("radius", 10.0)
                .execute()
                .path("clubsNear[*].name")
                .entityList(String.class)
                .containsExactly("Nearby Club")
                .path("clubsNear[*].distanceKm")
                .entityList(Double.class)
                .containsExactly(2.5);
    }

    @Test
    void shouldReturnLocationSuggestions() {
        LocationSuggestion suggestion = new LocationSuggestion(
                "Paris (75000) - Paris - France",
                "Paris",
                "75000",
                "Paris",
                "France",
                48.8566,
                2.3522
        );

        when(locationService.suggestLocations("Par"))
                .thenReturn(List.of(suggestion));

        String query = """
            query($term: String!) {
              suggestLocations(term: $term) {
                label
                city
              }
            }
            """;

        graphQlTester.document(query)
                .variable("term", "Par")
                .execute()
                .path("suggestLocations[0].city")
                .entity(String.class)
                .isEqualTo("Paris");
    }

    @Test
    void shouldResolveLocationFromCoordinates() {
        LocationSuggestion suggestion = new LocationSuggestion(
                "Paris (75000) - Paris - France",
                "Paris",
                "75000",
                "Paris",
                "France",
                48.8566,
                2.3522
        );

        when(locationService.reverseGeocode(48.8566, 2.3522))
                .thenReturn(Optional.of(suggestion));

        String query = """
            query($lat: Float!, $lon: Float!) {
              locationFromCoordinates(latitude: $lat, longitude: $lon) {
                label
                postalCode
              }
            }
            """;

        graphQlTester.document(query)
                .variable("lat", 48.8566)
                .variable("lon", 2.3522)
                .execute()
                .path("locationFromCoordinates.label")
                .entity(String.class)
                .isEqualTo("Paris (75000) - Paris - France");
    }
}

