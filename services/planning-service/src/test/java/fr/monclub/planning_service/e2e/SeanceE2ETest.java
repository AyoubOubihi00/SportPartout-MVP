package fr.monclub.planning_service.e2e;

import fr.monclub.planning_service.planning.catalog.SeanceCatalog;
import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class SeanceE2ETest {

    @Container
    public static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    // injecte dynamiquement les propriétés avant que Spring démarre
    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SeanceCatalog catalog;

    private UUID clubId;
    private UUID seanceId;

    @BeforeEach
    @Transactional
    void setUp() {
        clubId = UUID.randomUUID();

        Seance s = Seance.builder()
                .idClub(clubId)
                .idTerrain(UUID.randomUUID())
                .idCoach(UUID.randomUUID())
                .startTime(OffsetDateTime.parse("2025-01-01T10:00:00Z"))
                .endTime(OffsetDateTime.parse("2025-01-01T11:00:00Z"))
                .capacite(10)
                .capaSeance(3)
                .idSport(UUID.randomUUID())
                .nameSport("Foot")
                .description("Séance E2E")
                .status(SeanceStatus.OPEN)
                .build();

        seanceId = catalog.save(s).getId();
    }

    @Test
    void shouldFetchSeanceById_ThroughGraphQL() {
        String query = """
            query($id: UUID!) {
              seance(id: $id) {
                id
                nameSport
                status
                capacite
                capaSeance
              }
            }
        """;

        var response = postGraphQL(query, Map.<String, Object>of("id", seanceId.toString()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("\"nameSport\":\"Foot\"")
                .contains("\"status\":\"OPEN\"")
                .contains("\"capacite\":10")
                .contains("\"capaSeance\":3");
    }

    @Test
    void shouldFetchSeancesByClubBetween_ThroughGraphQL() {
        String query = """
            query($clubId: UUID!, $start: DateTime!, $end: DateTime!) {
              seancesByClubBetween(clubId: $clubId, start: $start, end: $end) {
                id
                nameSport
                status
              }
            }
        """;

        Map<String, Object> vars = Map.of(
                "clubId", clubId.toString(),
                "start",  "2025-01-01T00:00:00Z",
                "end",    "2025-01-02T00:00:00Z"
        );

        var response = postGraphQL(query, vars);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("\"nameSport\":\"Foot\"")
                .contains("\"status\":\"OPEN\"");
    }

    @Test
    void shouldJoinSeance_ThroughGraphQL() {
        String mutation = """
            mutation($id: UUID!) {
              joinSeance(id: $id) { id capaSeance status }
            }
        """;

        var response = postGraphQL(mutation, Map.<String, Object>of("id", seanceId.toString()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // capaSeance devait passer de 3 -> 4
        assertThat(response.getBody()).contains("\"capaSeance\":4");
    }

    @Test
    void shouldLeaveSeance_ThroughGraphQL() {
        // On commence par une inscription pour s'assurer qu'il y a au moins 1 inscrit
        String join = """
            mutation($id: UUID!) { joinSeance(id: $id) { id capaSeance } }
        """;
        postGraphQL(join, Map.<String, Object>of("id", seanceId.toString()));

        String leave = """
            mutation($id: UUID!) { leaveSeance(id: $id) { id capaSeance status } }
        """;
        var response = postGraphQL(leave, Map.<String, Object>of("id", seanceId.toString()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // capaSeance était 3 -> join => 4 -> leave => 3
        assertThat(response.getBody()).contains("\"capaSeance\":3");
    }

    // ----------------- util -----------------
    private ResponseEntity<String> postGraphQL(String query, Map<String, Object> variables) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of(
                "query", query,
                "variables", variables
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity("/graphql", entity, String.class);
    }
}
