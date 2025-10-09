package fr.monclub.auth_service.integration;

import fr.monclub.auth_service.auth.controller.UserController;
import fr.monclub.auth_service.auth.catalog.UserCatalog;
import fr.monclub.auth_service.auth.model.User;
import fr.monclub.auth_service.config.JWT.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
class UserControllerIntegrationTest {

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private UserCatalog userCatalog;

    @Test
    void shouldReturnCurrentUser() {
        // Configuration du SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("john.doe@example.com", null)
        );

        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("john.doe@example.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        when(userCatalog.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(mockUser));

        graphQlTester.document("""
            query { me { id email firstName lastName } }
        """)
                .execute()
                .path("me.email").entity(String.class).isEqualTo("john.doe@example.com")
                .path("me.firstName").entity(String.class).isEqualTo("John")
                .path("me.lastName").entity(String.class).isEqualTo("Doe")
                .path("me.id").entity(String.class).isEqualTo(mockUser.getId().toString());
    }
}
