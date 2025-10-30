package fr.monclub.auth_service.e2e;

import fr.monclub.auth_service.AuthServiceApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(
        classes = AuthServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"JWT_SECRET=w9vCCTEpDotAYUpZ12bhvcN7E2oB4Jied6826vwaemesAGyRULGHYbwP6C3aRk+0\n"}
)
@Testcontainers
class AuthControllerE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        this.baseUrl = "/graphql";
    }

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {
        postgreSQLContainer.start();
        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
    }


    private void registerUser(String email, String password) {
        String mutation = String.format("""
            mutation {
              register(input: {
                email: "%s",
                password: "%s",
                firstName: "Jane",
                lastName: "Doe",
                phone: "0123456789",
                birthDate: "1990-01-01T00:00:00Z"
              }) {
                message
              }
            }
        """, email, password);

        String body = "{ \"query\": \"" + mutation.replace("\n", " ").replace("\"", "\\\"") + "\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl, request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldRegisterUser() {
        registerUser("jane.doe2@example.com", "password123");
    }

    @Test
    void shouldLoginUser() {
        // On crée l'utilisateur avant de tester le login
        registerUser("jane.doe3@example.com", "password123");

        String mutation = """
            mutation {
              login(input: {
                email: "jane.doe3@example.com",
                password: "password123"
              }) {
                token
              }
            }
        """;

        String body = "{ \"query\": \"" + mutation.replace("\n", " ").replace("\"", "\\\"") + "\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl, request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        Map<String, String> login = (Map<String, String>) data.get("login");

        assertThat(login.get("token")).isNotNull();
        assertThat(login.get("token")).isNotEmpty();
    }


    @Test
    void shouldReturnCurrentUser() throws Exception {
        // 1️⃣ Enregistrer un utilisateur
        registerUser("andrew@example.fr", "Password123!");

        // 2️⃣ Se connecter pour obtenir le token
        String loginMutation = """
        mutation {
            login(input: {
                email: "andrew@example.fr",
                password: "Password123!"
            }) {
                token
            }
        }
    """;

        String loginBody = "{ \"query\": \"" + loginMutation.replace("\n", " ").replace("\"", "\\\"") + "\" }";

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> loginRequest = new HttpEntity<>(loginBody, loginHeaders);
        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(baseUrl, loginRequest, Map.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> loginData = (Map<String, Object>) loginResponse.getBody().get("data");
        Map<String, String> login = (Map<String, String>) loginData.get("login");
        String token = login.get("token");

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        // 3️⃣ Simuler la Gateway en envoyant les claims encodées en Base64
        //    (La Gateway fera normalement ça à ta place dans l’environnement réel)
        String claimsJson = """
        {
            "sub": "andrew@example.fr",
            "role": "USER"
        }
    """;

        String claimsBase64 = Base64.getEncoder().encodeToString(claimsJson.getBytes());

        String meQuery = """
        query {
            me {
                id
                email
                firstName
                lastName
            }
        }
    """;

        String meBody = "{ \"query\": \"" + meQuery.replace("\n", " ").replace("\"", "\\\"") + "\" }";

        HttpHeaders meHeaders = new HttpHeaders();
        meHeaders.setContentType(MediaType.APPLICATION_JSON);
        meHeaders.set("X-JWT-Claims", claimsBase64);

        HttpEntity<String> meRequest = new HttpEntity<>(meBody, meHeaders);
        ResponseEntity<Map> meResponse = restTemplate.postForEntity(baseUrl, meRequest, Map.class);

        assertThat(meResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> meData = (Map<String, Object>) meResponse.getBody().get("data");
        Map<String, Object> me = (Map<String, Object>) meData.get("me");

        assertThat(me.get("id")).isNotNull();
        assertThat(me.get("email")).isEqualTo("andrew@example.fr");
        assertThat(me.get("firstName")).isEqualTo("Jane");
        assertThat(me.get("lastName")).isEqualTo("Doe");
    }

}

