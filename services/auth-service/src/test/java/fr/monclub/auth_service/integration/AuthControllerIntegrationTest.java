package fr.monclub.auth_service.integration;

import fr.monclub.auth_service.auth.controller.AuthController;
import fr.monclub.auth_service.auth.mapper.RegisterInputToUserMapper;
import fr.monclub.auth_service.auth.model.RegisterInput;
import fr.monclub.auth_service.auth.model.LoginInput;
import fr.monclub.auth_service.auth.service.AuthService;
import fr.monclub.auth_service.config.JWT.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
class AuthControllerIntegrationTest {

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RegisterInputToUserMapper registerInputToUserMapper;

    @Test
    void shouldRegisterUser() {
        RegisterInput input = new RegisterInput();
        input.setEmail("john.doe@example.com");
        input.setPassword("password123");
        input.setFirstName("John");
        input.setLastName("Doe");
        input.setPhoneNumber("0123456789");
        input.setPhone("0123456789");
        input.setBirthDate(OffsetDateTime.parse("1990-01-01T00:00:00Z"));

        when(registerInputToUserMapper.apply(any())).thenReturn(new fr.monclub.auth_service.auth.model.User());
        when(authService.register(any())).thenReturn("User registered successfully");

        String mutation = """
            mutation {
              register(input: {
                email: "john.doe@example.com",
                password: "password123",
                firstName: "John",
                lastName: "Doe",
                phone: "0123456789",
                birthDate: "1990-01-01T00:00:00Z"
              }) {
                message
              }
            }
        """;

        graphQlTester.document(mutation)
                .execute()
                .path("register.message")
                .entity(String.class)
                .isEqualTo("User registered successfully");
    }

    @Test
    void shouldLoginUser() {
        when(authService.login("john.doe@example.com", "password123")).thenReturn("token123");

        String mutation = """
            mutation {
              login(input: {
                email: "john.doe@example.com",
                password: "password123"
              }) {
                token
              }
            }
        """;

        graphQlTester.document(mutation)
                .execute()
                .path("login.token")
                .entity(String.class)
                .isEqualTo("token123");
    }
}
