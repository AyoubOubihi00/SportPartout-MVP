package fr.monclub.planning_service.integration;

import fr.monclub.planning_service.config.GraphQLConfig;
import fr.monclub.planning_service.planning.catalog.SeanceCatalog;
import fr.monclub.planning_service.planning.controller.SeanceController;
import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import fr.monclub.planning_service.planning.model.SeanceWithAvailability;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@GraphQlTest(SeanceController.class)
@Import(GraphQLConfig.class)
class SeanceControllerIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private SeanceCatalog catalog;

    @Test
    void shouldJoinSeanceMutation() {
        UUID id = UUID.randomUUID();
        Seance joined = Seance.builder()
                .id(id)
                .idClub(UUID.randomUUID())
                .idTerrain(UUID.randomUUID())
                .startTime(OffsetDateTime.parse("2025-01-01T10:00:00Z"))
                .endTime(OffsetDateTime.parse("2025-01-01T11:00:00Z"))
                .capacite(5)
                .capaSeance(4)
                .idSport(UUID.randomUUID())
                .nameSport("Foot")
                .status(SeanceStatus.OPEN)
                .build();

        when(catalog.join(id)).thenReturn(joined);

        String mutation = """
            mutation($id: UUID!) {
              joinSeance(id: $id) {
                id
                capaSeance
                status
              }
            }
        """;

        graphQlTester.document(mutation)
                .variable("id", id)
                .execute()
                .path("joinSeance.id").entity(String.class).isEqualTo(id.toString())
                .path("joinSeance.capaSeance").entity(Integer.class).isEqualTo(4)
                .path("joinSeance.status").entity(String.class).isEqualTo("OPEN");
    }

    @Test
    void shouldLeaveSeanceMutation() {
        UUID id = UUID.randomUUID();
        Seance left = Seance.builder()
                .id(id)
                .idClub(UUID.randomUUID())
                .idTerrain(UUID.randomUUID())
                .startTime(OffsetDateTime.parse("2025-01-01T10:00:00Z"))
                .endTime(OffsetDateTime.parse("2025-01-01T11:00:00Z"))
                .capacite(5)
                .capaSeance(2)
                .idSport(UUID.randomUUID())
                .nameSport("Foot")
                .status(SeanceStatus.OPEN)
                .build();

        when(catalog.leave(id)).thenReturn(left);

        String mutation = """
            mutation($id: UUID!) {
              leaveSeance(id: $id) {
                id
                capaSeance
                status
              }
            }
        """;

        graphQlTester.document(mutation)
                .variable("id", id)
                .execute()
                .path("leaveSeance.id").entity(String.class).isEqualTo(id.toString())
                .path("leaveSeance.capaSeance").entity(Integer.class).isEqualTo(2)
                .path("leaveSeance.status").entity(String.class).isEqualTo("OPEN");
    }

    @Test
    void shouldReturnSeanceById() {
        UUID id = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        UUID terrainId = UUID.randomUUID();
        UUID sportId = UUID.randomUUID();

        Seance s = Seance.builder()
                .id(id)
                .idClub(clubId)
                .idTerrain(terrainId)
                .idCoach(null)
                .startTime(OffsetDateTime.parse("2025-01-01T10:00:00Z"))
                .endTime(OffsetDateTime.parse("2025-01-01T11:00:00Z"))
                .capacite(10)
                .capaSeance(3)
                .idSport(sportId)
                .nameSport("Foot")
                .description("Séance de test")
                .status(SeanceStatus.OPEN)
                .build();

        when(catalog.getById(id)).thenReturn(s);

        String query = """
            query($id: UUID!) {
              seance(id: $id) {
                id
                idClub
                idTerrain
                status
                capacite
                capaSeance
                nameSport
              }
            }
        """;

        graphQlTester.document(query)
                .variable("id", id)
                .execute()
                .path("seance.id").entity(String.class).isEqualTo(id.toString())
                .path("seance.idClub").entity(String.class).isEqualTo(clubId.toString())
                .path("seance.idTerrain").entity(String.class).isEqualTo(terrainId.toString())
                .path("seance.status").entity(String.class).isEqualTo("OPEN")
                .path("seance.capacite").entity(Integer.class).isEqualTo(10)
                .path("seance.capaSeance").entity(Integer.class).isEqualTo(3)
                .path("seance.nameSport").entity(String.class).isEqualTo("Foot");
    }

    @Test
    void shouldReturnSeancesByClubBetween() {
        UUID clubId = UUID.randomUUID();
        String start = "2025-01-01T00:00:00Z";
        String end   = "2025-01-02T00:00:00Z";

        Seance a = Seance.builder()
                .id(UUID.randomUUID())
                .idClub(clubId)
                .idTerrain(UUID.randomUUID())
                .startTime(OffsetDateTime.parse("2025-01-01T10:00:00Z"))
                .endTime(OffsetDateTime.parse("2025-01-01T11:00:00Z"))
                .capacite(8).capaSeance(4)
                .idSport(UUID.randomUUID()).nameSport("Tennis")
                .status(SeanceStatus.OPEN)
                .build();

        Seance b = Seance.builder()
                .id(UUID.randomUUID())
                .idClub(clubId)
                .idTerrain(UUID.randomUUID())
                .startTime(OffsetDateTime.parse("2025-01-01T15:00:00Z"))
                .endTime(OffsetDateTime.parse("2025-01-01T16:30:00Z"))
                .capacite(12).capaSeance(12)
                .idSport(UUID.randomUUID()).nameSport("Foot")
                .status(SeanceStatus.FULL)
                .build();

        when(catalog.findByClubBetween(
                clubId,
                OffsetDateTime.parse(start),
                OffsetDateTime.parse(end)
        )).thenReturn(List.of(a, b));

        String query = """
            query($clubId: UUID!, $start: DateTime!, $end: DateTime!) {
              seancesByClubBetween(clubId: $clubId, start: $start, end: $end) {
                id
                status
                nameSport
              }
            }
        """;

        graphQlTester.document(query)
                .variable("clubId", clubId)
                .variable("start", start)
                .variable("end", end)
                .execute()
                .path("seancesByClubBetween[*].status")
                .entityList(String.class)
                .containsExactly("OPEN", "FULL"); 
    }

    @Test
    void shouldReturnSeancesByCoachBetween() {
        UUID coachId = UUID.randomUUID();
        String start = "2025-01-01T00:00:00Z";
        String end   = "2025-01-02T00:00:00Z";

        Seance s = Seance.builder()
                .id(UUID.randomUUID())
                .idClub(UUID.randomUUID())
                .idTerrain(UUID.randomUUID())
                .idCoach(coachId)
                .startTime(OffsetDateTime.parse("2025-01-01T09:00:00Z"))
                .endTime(OffsetDateTime.parse("2025-01-01T10:30:00Z"))
                .capacite(10).capaSeance(5)
                .idSport(UUID.randomUUID()).nameSport("Basket")
                .status(SeanceStatus.OPEN)
                .build();

        when(catalog.findByCoachBetween(
                coachId, OffsetDateTime.parse(start), OffsetDateTime.parse(end)
        )).thenReturn(List.of(s));

        String query = """
            query($coachId: UUID!, $start: DateTime!, $end: DateTime!) {
              seancesByCoachBetween(coachId: $coachId, start: $start, end: $end) {
                id
                nameSport
              }
            }
        """;

        graphQlTester.document(query)
                .variable("coachId", coachId)
                .variable("start", start)
                .variable("end", end)
                .execute()
                .path("seancesByCoachBetween[0].nameSport")
                .entity(String.class)
                .isEqualTo("Basket");
    }

    @Test
    void shouldReturnSeancesWithAvailabilityByClubBetween() {
        UUID clubId = UUID.randomUUID();
        String start = "2025-01-01T00:00:00Z";
        String end   = "2025-01-02T00:00:00Z";

        Seance base = Seance.builder()
                .id(UUID.randomUUID())
                .idClub(clubId)
                .idTerrain(UUID.randomUUID())
                .startTime(OffsetDateTime.parse("2025-01-01T13:00:00Z"))
                .endTime(OffsetDateTime.parse("2025-01-01T14:00:00Z"))
                .capacite(6).capaSeance(4)
                .idSport(UUID.randomUUID()).nameSport("Foot")
                .status(SeanceStatus.OPEN)
                .build();

        var dto = SeanceWithAvailability.fromSeance(base);

        when(catalog.findWithAvailabilityByClubBetween(
                clubId, OffsetDateTime.parse(start), OffsetDateTime.parse(end)
        )).thenReturn(List.of(dto));

        String query = """
            query($clubId: UUID!, $start: DateTime!, $end: DateTime!) {
              seancesWithAvailabilityByClubBetween(clubId: $clubId, start: $start, end: $end) {
                id
                nameSport
                remainingPlaces
                full
              }
            }
        """;

        graphQlTester.document(query)
                .variable("clubId", clubId)
                .variable("start", start)
                .variable("end", end)
                .execute()
                .path("seancesWithAvailabilityByClubBetween[0].nameSport")
                .entity(String.class).isEqualTo("Foot")
                .path("seancesWithAvailabilityByClubBetween[0].remainingPlaces")
                .entity(Integer.class).isEqualTo(2)
                .path("seancesWithAvailabilityByClubBetween[0].full")
                .entity(Boolean.class).isEqualTo(false);
    }
}
