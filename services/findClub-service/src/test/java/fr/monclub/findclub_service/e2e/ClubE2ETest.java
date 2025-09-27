package fr.monclub.findclub_service.e2e;

import fr.monclub.findclub_service.club.model.Club;
import fr.monclub.findclub_service.club.catalog.FindClubCatalog;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClubE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FindClubCatalog clubCatalog;

    @BeforeEach
    @Transactional
    void setUp() {
        Club club = new Club();
        club.setName("E2E Club");
        club.setTypeIdSport(UUID.randomUUID());
        club.setTypeNameSport("football");
        club.setCity("Paris");
        club.setCountry("France");
        club.setAddress("1 rue du Test");
        club.setLatitude(48.8566);
        club.setLongitude(2.3522);

        clubCatalog.save(club);
    }

    @Test
    void shouldFetchClubsThroughGraphQL() {
        String graphQLQuery = "query { clubs { name city } }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("query", graphQLQuery);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/graphql", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("E2E Club")
                .contains("Paris");
    }

    @Test
    void shouldFetchNearbyClubsThroughGraphQL() {
        String graphQLQuery = "query { clubsNear(latitude: 48.8566, longitude: 2.3522, radiusKm: 5) { name city } }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("query", graphQLQuery);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/graphql", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("E2E Club")
                .contains("Paris");
    }
}
