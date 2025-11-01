package fr.monclub.bff.controller;

import fr.monclub.bff.config.GraphQLConfig;
import fr.monclub.bff.model.SeanceStatus;
import fr.monclub.bff.service.SeanceDecisionBffService;
import fr.monclub.bff.service.SeanceDecisionBffService.DecisionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@GraphQlTest(SeanceDecisionGraphQlController.class)
@Import(GraphQLConfig.class)
class SeanceDecisionGraphQlControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private SeanceDecisionBffService decisionService;

    @Test
    void acceptPendingSeanceShouldReturnDecision() {
        UUID seanceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();

        when(decisionService.accept(eq(seanceId), eq(userId)))
                .thenReturn(new DecisionResponse(seanceId, clubId, SeanceStatus.OPEN, null));

        String mutation = """
            mutation($seanceId: UUID!, $userId: UUID!) {
              acceptPendingSeance(seanceId: $seanceId, userId: $userId) {
                id
                status
              }
            }
        """;

        graphQlTester.document(mutation)
                .variable("seanceId", seanceId)
                .variable("userId", userId)
                .execute()
                .path("acceptPendingSeance.id")
                .entity(String.class)
                .isEqualTo(seanceId.toString())
                .path("acceptPendingSeance.status")
                .entity(String.class)
                .isEqualTo(SeanceStatus.OPEN.name());
    }

    @Test
    void refusePendingSeanceShouldReturnDecision() {
        UUID seanceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        String reason = "Coach indisponible";

        when(decisionService.refuse(eq(seanceId), eq(userId), eq(reason)))
                .thenReturn(new DecisionResponse(seanceId, clubId, SeanceStatus.CANCELED, reason));

        String mutation = """
            mutation($seanceId: UUID!, $userId: UUID!, $reason: String!) {
              refusePendingSeance(seanceId: $seanceId, userId: $userId, reason: $reason) {
                id
                status
                refusalReason
              }
            }
        """;

        graphQlTester.document(mutation)
                .variable("seanceId", seanceId)
                .variable("userId", userId)
                .variable("reason", reason)
                .execute()
                .path("refusePendingSeance.status")
                .entity(String.class)
                .isEqualTo(SeanceStatus.CANCELED.name())
                .path("refusePendingSeance.refusalReason")
                .entity(String.class)
                .isEqualTo(reason);
    }
}
