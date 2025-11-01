package fr.monclub.planning_service.integration;

import fr.monclub.planning_service.config.GraphQLConfig;
import fr.monclub.planning_service.planning.controller.SeanceMutationController;
import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import fr.monclub.planning_service.planning.service.SeanceDecisionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@GraphQlTest(SeanceMutationController.class)
@Import(GraphQLConfig.class)
class SeanceMutationControllerIntegrationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private SeanceDecisionService decisionService;

    @Test
    void shouldAcceptSeance() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        UUID terrainId = UUID.randomUUID();
        UUID sportId = UUID.randomUUID();

        Seance accepted = Seance.builder()
                .id(seanceId)
                .idClub(clubId)
                .idTerrain(terrainId)
                .startTime(OffsetDateTime.parse("2025-02-01T10:00:00Z"))
                .endTime(OffsetDateTime.parse("2025-02-01T11:30:00Z"))
                .capacite(10)
                .capaSeance(5)
                .idSport(sportId)
                .nameSport("Foot")
                .status(SeanceStatus.OPEN)
                .build();

        when(decisionService.accept(seanceId, clubId)).thenReturn(accepted);

        String mutation = """
            mutation($seanceId: UUID!, $clubId: UUID!) {
              acceptSeance(seanceId: $seanceId, clubId: $clubId) {
                id
                status
                nameSport
              }
            }
        """;

        graphQlTester.document(mutation)
                .variable("seanceId", seanceId)
                .variable("clubId", clubId)
                .execute()
                .path("acceptSeance.id")
                .entity(String.class)
                .isEqualTo(seanceId.toString())
                .path("acceptSeance.status")
                .entity(String.class)
                .isEqualTo("OPEN")
                .path("acceptSeance.nameSport")
                .entity(String.class)
                .isEqualTo("Foot");
    }

    @Test
    void shouldRefuseSeance() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        UUID terrainId = UUID.randomUUID();
        UUID sportId = UUID.randomUUID();

        String reason = "Coach indisponible";

        Seance refused = Seance.builder()
                .id(seanceId)
                .idClub(clubId)
                .idTerrain(terrainId)
                .startTime(OffsetDateTime.parse("2025-03-05T09:00:00Z"))
                .endTime(OffsetDateTime.parse("2025-03-05T10:00:00Z"))
                .capacite(8)
                .capaSeance(1)
                .idSport(sportId)
                .nameSport("Tennis")
                .status(SeanceStatus.CANCELED)
                .refusalReason(reason)
                .build();

        when(decisionService.refuse(seanceId, clubId, reason)).thenReturn(refused);

        String mutation = """
            mutation($seanceId: UUID!, $clubId: UUID!, $reason: String!) {
              refuseSeance(seanceId: $seanceId, clubId: $clubId, reason: $reason) {
                id
                status
                nameSport
                refusalReason
              }
            }
        """;

        graphQlTester.document(mutation)
                .variable("seanceId", seanceId)
                .variable("clubId", clubId)
                .variable("reason", reason)
                .execute()
                .path("refuseSeance.status")
                .entity(String.class)
                .isEqualTo("CANCELED")
                .path("refuseSeance.nameSport")
                .entity(String.class)
                .isEqualTo("Tennis")
                .path("refuseSeance.refusalReason")
                .entity(String.class)
                .isEqualTo(reason);
    }
}
