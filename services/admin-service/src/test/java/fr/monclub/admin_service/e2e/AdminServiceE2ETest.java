package fr.monclub.admin_service.e2e;

import fr.monclub.admin_service.admin.model.SportType;
import fr.monclub.admin_service.admin.catalog.SportTypeCatalog;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminServiceE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SportTypeCatalog sportTypeCatalog;

    private UUID sportTypeId;

    @BeforeEach
    @Transactional
    void setUp() {
        SportType sportType = new SportType();
        sportType.setId(UUID.randomUUID());
        sportType.setName("Football");

        SportType sportType2 = new SportType();
        sportType2.setId(UUID.randomUUID());
        sportType2.setName("Basketball");
        sportTypeCatalog.save(sportType2);

        SportType saved = sportTypeCatalog.save(sportType);
        sportTypeId = saved.getId();
    }

    @Test
    void shouldFetchClubById_throughGraphQL() {
        String graphQLQuery = "query { sportTypeById(id: \"%s\") { id name } }".formatted(sportTypeId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("query", graphQLQuery);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/graphql", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("Football")
                .contains(sportTypeId.toString());
    }


    @Test
    void shouldFetchAllClubs_throughGraphQL() {
        String graphQLQuery = "query { sportTypes { id name } }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("query", graphQLQuery);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/graphql", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("Football")
                .contains("Basketball");
    }

    @Test
    void shouldCreateSportType_throughGraphQL() {
        String graphQLMutation = "mutation { createSportType(name: \"Tennis\") { id name } }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("query", graphQLMutation);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/graphql", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("Tennis");

        // Verify that the sport type was actually created in the catalog
        SportType createdSportType = sportTypeCatalog.getAllSportTypes().stream()
                .filter(st -> "Tennis".equals(st.getName()))
                .findFirst()
                .orElse(null);

        assertThat(createdSportType).isNotNull();
        assertThat(createdSportType.getName()).isEqualTo("Tennis");
    }

    @Test
    void shouldUpdateSportType_throughGraphQL() {
        String graphQLMutation = "mutation { updateSportType(id: \"%s\", name: \"Soccer\") { id name } }".formatted(sportTypeId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("query", graphQLMutation);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/graphql", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("Soccer")
                .contains(sportTypeId.toString());

        // Verify that the sport type was actually updated in the catalog
        SportType updatedSportType = sportTypeCatalog.getSportTypeById(sportTypeId);
        assertThat(updatedSportType).isNotNull();
        assertThat(updatedSportType.getName()).isEqualTo("Soccer");
    }




}

