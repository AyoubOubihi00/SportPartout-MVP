package fr.monclub.club_service.e2e;

import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.SportsType;
import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.fixtures.ClubFixtures;
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
class ClubE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClubCatalog clubCatalog;

    private UUID clubId;

    @BeforeEach
    @Transactional
    void setUp() {
        Club club = new Club();
        club.setName("E2E Club");
        club.setTypeId(ClubFixtures.UUID_FOOTBALL);
        club.setTypeName("FOOTBALL");

        Club savedClub = clubCatalog.save(club);
        clubId = savedClub.getId();
    }

    @Test
    void shouldFetchClubById_throughGraphQL() {
        String graphQLQuery = "query { clubById(id: \"%s\") { id name typeId typeName } }".formatted(clubId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("query", graphQLQuery);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/graphql", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("E2E Club")
                .contains("FOOTBALL");
    }


    @Test
    void shouldFetchAllClubs_throughGraphQL() {
        String graphQLQuery = "query { clubs { id name typeId typeName } }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("query", graphQLQuery);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/graphql", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("E2E Club")
                .contains("FOOTBALL");
    }

    @Test
    void shouldCreateClub_throughGraphQL() {
        String graphQLMutation = """
                mutation {
                  createClub(data: {
                    name: "New Club",
                    city: "City",
                    address: "123 Street",
                    country: "Country",
                    phoneNumber: "1234567890",
                    email: "email@exemple.com",
                    latitude: 45.0,
                    longitude: 90.0,
                    typeId: "%s",
                    typeName: "%s"
                    }, ownerId: "%s") {
                    id
                    name
                    typeId
                    typeName
                    }
                    }
                """.formatted(ClubFixtures.UUID_FOOTBALL, "football", UUID.randomUUID());


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of("query", graphQLMutation);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/graphql", entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains("New Club")
                .contains("football");

        Club createdClub = clubCatalog.getAllClubs().stream()
                .filter(c -> "New Club".equals(c.getName()))
                .findFirst()
                .orElse(null);
        assertThat(createdClub).isNotNull();
        assertThat(createdClub.getTypeName()).isEqualTo("football");


    }

    @Test
    @Transactional
    void shouldCreateThenUpdateClub_throughGraphQL() {
        UUID ownerId = UUID.randomUUID();

        String createMutation = """
            mutation {
              createClub(
                data: {
                  name: "Temp Club",
                  city: "Initial City",
                  address: "123 Street",
                  country: "Initial Country",
                  phoneNumber: "1111111111",
                  email: "temp@email.com",
                  latitude: 10.0,
                  longitude: 20.0,
                  typeId: "%s",
                  typeName: "football"
                },
                ownerId: "%s"
              ) {
                id
                name
                city
                typeName
              }
            }
            """.formatted(ClubFixtures.UUID_FOOTBALL, ownerId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> createBody = Map.of("query", createMutation);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createBody, headers);

        ResponseEntity<Map> createResponse = restTemplate.postForEntity("/graphql", createEntity, Map.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> data = (Map<String, Object>) createResponse.getBody().get("data");
        Map<String, Object> createdClub = (Map<String, Object>) data.get("createClub");
        String createdId = (String) createdClub.get("id");

        assertThat(createdClub.get("name")).isEqualTo("Temp Club");
        assertThat(createdClub.get("city")).isEqualTo("Initial City");

        String updateMutation = """
            mutation {
              updateClub(
                id: "%s",
                data: {
                  name: "Updated Club",
                  city: "Updated City",
                  address: "456 Avenue",
                  country: "Updated Country",
                  phoneNumber: "2222222222",
                  email: "updated@email.com",
                  latitude: 30.0,
                  longitude: 40.0,
                  typeId: "%s",
                  typeName: "basketball"
                },
                ownerId: "%s"
              ) {
                id
                name
                city
                typeName
              }
            }
            """.formatted(createdId, ClubFixtures.UUID_BASKETBALL, ownerId);

        Map<String, Object> updateBody = Map.of("query", updateMutation);
        HttpEntity<Map<String, Object>> updateEntity = new HttpEntity<>(updateBody, headers);

        ResponseEntity<String> updateResponse = restTemplate.postForEntity("/graphql", updateEntity, String.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody())
                .contains("Updated Club")
                .contains("Updated City")
                .contains("basketball");

        Club updated = clubCatalog.getClubById(UUID.fromString(createdId));
        assertThat(updated.getName()).isEqualTo("Updated Club");
        assertThat(updated.getCity()).isEqualTo("Updated City");
        assertThat(updated.getTypeName()).isEqualTo("basketball");
    }

    @Test
    @Transactional
    void shouldCreateThenDisableClub_throughGraphQL() {
        UUID ownerId = UUID.randomUUID();

        String createMutation = """
            mutation {
              createClub(
                data: {
                  name: "Temp Club",
                  city: "Initial City",
                  address: "123 Street",
                  country: "Initial Country",
                  phoneNumber: "1111111111",
                  email: "temp@email.com",
                  latitude: 10.0,
                  longitude: 20.0,
                  typeId: "%s",
                  typeName: "football"
                },
                ownerId: "%s"
              ) {
                id
                name
                city
                typeName
              }
            }
            """.formatted(ClubFixtures.UUID_FOOTBALL, ownerId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> createBody = Map.of("query", createMutation);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createBody, headers);

        ResponseEntity<Map> createResponse = restTemplate.postForEntity("/graphql", createEntity, Map.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> data = (Map<String, Object>) createResponse.getBody().get("data");
        Map<String, Object> createdClub = (Map<String, Object>) data.get("createClub");
        String createdId = (String) createdClub.get("id");

        assertThat(createdClub.get("name")).isEqualTo("Temp Club");
        assertThat(createdClub.get("city")).isEqualTo("Initial City");


        // 3. Désactiver le club
        String disableMutation = """
        mutation {
          disableClub(
            id: "%s",
            ownerId: "%s"
          )
        }
        """.formatted(createdId, ownerId);

        Map<String, Object> disableBody = Map.of("query", disableMutation);
        HttpEntity<Map<String, Object>> disableEntity = new HttpEntity<>(disableBody, headers);

        ResponseEntity<String> disableResponse = restTemplate.postForEntity("/graphql", disableEntity, String.class);

        assertThat(disableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(disableResponse.getBody()).contains("\"disableClub\":true");


        // 4. Vérifier que le club a bien été désactivé en base
        Club disabledClub = clubCatalog.getClubById(UUID.fromString(createdId));
        assertThat(disabledClub.isActive()).isFalse();
        assertThat(disabledClub.getDeletedAt()).isNotNull();
        assertThat(disabledClub.getName()).isEqualTo("Temp Club"); // Les autres données restent inchangées


    }

    @Test
    @Transactional
    void shouldCreateThenDisableClubWhenFakeOwner_throughGraphQL() {
        UUID ownerId = UUID.randomUUID();

        String createMutation = """
            mutation {
              createClub(
                data: {
                  name: "Temp Club",
                  city: "Initial City",
                  address: "123 Street",
                  country: "Initial Country",
                  phoneNumber: "1111111111",
                  email: "temp@email.com",
                  latitude: 10.0,
                  longitude: 20.0,
                  typeId: "%s",
                  typeName: "football"
                },
                ownerId: "%s"
              ) {
                id
                name
                city
                typeName
              }
            }
            """.formatted(ClubFixtures.UUID_FOOTBALL, ownerId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> createBody = Map.of("query", createMutation);
        HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(createBody, headers);

        ResponseEntity<Map> createResponse = restTemplate.postForEntity("/graphql", createEntity, Map.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> data = (Map<String, Object>) createResponse.getBody().get("data");
        Map<String, Object> createdClub = (Map<String, Object>) data.get("createClub");
        String createdId = (String) createdClub.get("id");

        assertThat(createdClub.get("name")).isEqualTo("Temp Club");
        assertThat(createdClub.get("city")).isEqualTo("Initial City");


        // 3. Désactiver le club
        String disableMutation = """
        mutation {
          disableClub(
            id: "%s",
            ownerId: "%s"
          )
        }
        """.formatted(createdId, UUID.randomUUID());



        Map<String, Object> disableBody = Map.of("query", disableMutation);
        HttpEntity<Map<String, Object>> disableEntity = new HttpEntity<>(disableBody, headers);

        ResponseEntity<String> disableResponse = restTemplate.postForEntity("/graphql", disableEntity, String.class);

        assertThat(disableResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Vérifier que la réponse contient l'erreur d'autorisation
        String responseBody = disableResponse.getBody();
        assertThat(responseBody)
                .contains("\"errors\"")
                .contains("Vous n'êtes pas autorisé à modifier ce club")
                .contains("\"disableClub\":null");
        // Le club devrait toujours être actif après l'échec
        Club stillActiveClub = clubCatalog.getClubById(UUID.fromString(createdId));
        assertThat(stillActiveClub.isActive()).isTrue();
        assertThat(stillActiveClub.getDeletedAt()).isNull();

    }



}

