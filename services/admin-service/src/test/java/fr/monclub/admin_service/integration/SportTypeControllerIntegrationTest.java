package fr.monclub.admin_service.integration;

import fr.monclub.admin_service.admin.model.SportType;
import fr.monclub.admin_service.admin.catalog.SportTypeCatalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
class SportTypeControllerIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private SportTypeCatalog sportTypeCatalog;


    @Test
    void shouldReturnAllClubs() {
        SportType c1 = new SportType();
        c1.setId(UUID.randomUUID());
        c1.setName("Sport1");

        SportType c2 = new SportType();
        c2.setId(UUID.randomUUID());
        c2.setName("Sport2");

        when(sportTypeCatalog.getAllSportTypes()).thenReturn(List.of(c1, c2));

        String query = """
                query {
                  sportTypes {
                    name
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("sportTypes[*].name")
                .entityList(String.class)
                .containsExactly("Sport1", "Sport2");
    }

    @Test
    void shouldReturnClubById() {
        UUID clubId = UUID.randomUUID();
        SportType c1 = new SportType();
        c1.setId(clubId);
        c1.setName("Sport1");

        when(sportTypeCatalog.getSportTypeById(clubId)).thenReturn(c1);

        String query = """
            query($id: UUID!) {
              sportTypeById(id: $id) {
                name
              }
            }
            """;

        graphQlTester.document(query)
                .variable("id", clubId)
                .execute()
                .path("sportTypeById.name")
                .entity(String.class)
                .isEqualTo("Sport1");
    }

    @Test
    void shouldCreateSportType() {
        UUID sportTypeId = UUID.randomUUID();
        String sportTypeName = "Sport1";
        SportType c1 = new SportType();
        c1.setId(sportTypeId);
        c1.setName(sportTypeName);

        when(sportTypeCatalog.save(any(SportType.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(sportTypeCatalog.getSportTypeById(sportTypeId)).thenReturn(c1);

        String mutation = """
            mutation($name: String!) {
              createSportType(name: $name) {
                name
              }
            }
            """;

        graphQlTester.document(mutation)
                .variable("name", sportTypeName)
                .execute()
                .path("createSportType.name")
                .entity(String.class)
                .isEqualTo(sportTypeName);
    }

    @Test
    void shouldUpdateSportType() {
        UUID sportTypeId = UUID.randomUUID();
        String oldName = "Old Sport";
        String newName = "New Sport";

        SportType existingSportType = new SportType();
        existingSportType.setId(sportTypeId);
        existingSportType.setName(oldName);

        when(sportTypeCatalog.getSportTypeById(sportTypeId)).thenReturn(existingSportType);
        when(sportTypeCatalog.save(any(SportType.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String mutation = """
            mutation($id: UUID!, $name: String!) {
              updateSportType(id: $id, name: $name) {
                name
              }
            }
            """;

        graphQlTester.document(mutation)
                .variable("id", sportTypeId)
                .variable("name", newName)
                .execute()
                .path("updateSportType.name")
                .entity(String.class)
                .isEqualTo(newName);
    }






}
