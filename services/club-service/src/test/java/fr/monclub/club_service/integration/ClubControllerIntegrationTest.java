package fr.monclub.club_service.integration;

import fr.monclub.club_service.club.controller.ClubController;
import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.service.AddClubService;
import fr.monclub.club_service.club.service.DeleteClubService;
import fr.monclub.club_service.club.service.ReactivateClubService;
import fr.monclub.club_service.club.service.UpdateClubService;
import fr.monclub.club_service.config.GraphQLConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@GraphQlTest(ClubController.class)
@Import({UpdateClubService.class, GraphQLConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClubControllerIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private ClubCatalog clubCatalog;

    @MockitoBean
    private UpdateClubService updateClubService;

    @MockitoBean
    private DeleteClubService deleteClubService;

    @MockitoBean
    private AddClubService addClubService;

    @MockitoBean
    private ReactivateClubService reactivateClubService;


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
    void shouldReturnClubById() {
        UUID clubId = UUID.randomUUID();
        Club c1 = new Club();
        c1.setId(clubId);
        c1.setName("Club1");

        when(clubCatalog.getClubById(clubId)).thenReturn(c1);

        String query = """
            query($id: UUID!) {
              clubById(id: $id) {
                name
              }
            }
            """;

        graphQlTester.document(query)
                .variable("id", clubId)
                .execute()
                .path("clubById.name")
                .entity(String.class)
                .isEqualTo("Club1");
    }

    @Test
    void shouldReturnByOwnerId() {
        UUID ownerId = UUID.randomUUID();
        Club c1 = new Club();
        c1.setId(UUID.randomUUID());
        c1.setOwnerId(ownerId);
        c1.setName("Club1");

        Club c2 = new Club();
        c2.setId(UUID.randomUUID());
        c2.setOwnerId(ownerId);
        c2.setName("Club2");

        when(clubCatalog.getClubsByOwnerId(ownerId)).thenReturn(List.of(c1, c2));

        String query = """
            query($id: UUID!) {
              clubsByOwnerId(id: $id) {
                name
              }
            }
            """;

        graphQlTester.document(query)
                .variable("id", ownerId)
                .execute()
                .path("clubsByOwnerId[*].name")
                .entityList(String.class)
                .containsExactly("Club1", "Club2");
    }


    @Test
    void shouldUpdateClub() {
        UUID clubId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        Club existingClub = new Club();
        existingClub.setId(clubId);
        existingClub.setOwnerId(ownerId);
        existingClub.setName("Old Name");

        Club updatedClub = new Club();
        updatedClub.setId(clubId);
        updatedClub.setOwnerId(ownerId);
        updatedClub.setName("New Name");

        when(clubCatalog.getClubById(clubId)).thenReturn(existingClub);
        when(clubCatalog.save(existingClub)).thenReturn(updatedClub);
        // On mock directement le service utilisé par le controller
        when(updateClubService.apply(eq(clubId), any(), eq(ownerId)))
                .thenReturn(updatedClub);

        String mutation = """
        mutation($id: UUID!, $data: UpdateClubInput!, $ownerId: UUID!) {
          updateClub(id: $id, data: $data, ownerId: $ownerId) {
            name
          }
        }
        """;

        Map<String, Object> input = Map.of(
                "name", "New Name"
        );

        graphQlTester.document(mutation)
                .variable("id", clubId)
                .variable("data", input)
                .variable("ownerId", ownerId)
                .execute()
                .path("updateClub.name")
                .entity(String.class)
                .isEqualTo("New Name");
    }

    @Test
    void shouldCreateClub() {
        UUID clubId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        Club createdClub = new Club();
        createdClub.setId(clubId);
        createdClub.setOwnerId(ownerId);
        createdClub.setName("My New Club");

        // On simule le service de création
        when(addClubService.apply(any(), eq(ownerId)))
                .thenReturn(createdClub);

        String mutation = """
    mutation($data: UpdateClubInput!, $ownerId: UUID!) {
      createClub(data: $data, ownerId: $ownerId) {
        id
        name
      }
    }
    """;

        Map<String, Object> input = Map.of(
                "name", "My New Club"
        );

        graphQlTester.document(mutation)
                .variable("data", input)
                .variable("ownerId", ownerId)
                .execute()
                .path("createClub.name")
                .entity(String.class)
                .isEqualTo("My New Club")
                .path("createClub.id")
                .entity(UUID.class)
                .isEqualTo(clubId);
    }

    @Test
    void shouldDisableClub_whenOwnerMatches() {
        UUID clubId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        // Mock directement le service
        when(deleteClubService.apply(clubId, ownerId)).thenReturn(true);

        String mutation = """
        mutation($id: UUID!, $ownerId: UUID!) {
          disableClub(id: $id, ownerId: $ownerId)
        }
        """;

        graphQlTester.document(mutation)
                .variable("id", clubId)
                .variable("ownerId", ownerId)
                .execute()
                .path("disableClub")
                .entity(Boolean.class)
                .isEqualTo(true);

        // Vérifier que le service a été appelé
        verify(deleteClubService).apply(clubId, ownerId);
    }





}
