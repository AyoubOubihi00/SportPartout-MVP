package fr.monclub.bff.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import fr.monclub.bff.client.GraphQlClient;
import fr.monclub.bff.client.GraphQlClientException;
import fr.monclub.bff.exception.BadRequestException;
import fr.monclub.bff.exception.ExternalServiceException;
import fr.monclub.bff.exception.ForbiddenOperationException;
import fr.monclub.bff.exception.ResourceNotFoundException;
import fr.monclub.bff.model.SeanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanningGateway {

    private static final String SEANCE_FRAGMENT = """
            id
            idClub
            status
            refusalReason
            """;

    @Qualifier("planningGraphQlClient")
    private final GraphQlClient planningGraphQlClient;

    public SeanceDto getSeance(UUID seanceId) {
        String query = """
                query($id: UUID!) {
                  seance(id: $id) {
                    %s
                  }
                }
                """.formatted(SEANCE_FRAGMENT);
        try {
            JsonNode data = planningGraphQlClient.execute(query, Map.of("id", seanceId.toString()));
            JsonNode seanceNode = data.get("seance");
            if (seanceNode == null || seanceNode.isNull()) {
                throw new ResourceNotFoundException("Seance %s introuvable".formatted(seanceId));
            }
            return mapSeance(seanceNode);
        } catch (GraphQlClientException ex) {
            throw translatePlanningException(ex, "Seance %s introuvable".formatted(seanceId));
        }
    }

    public SeanceDto accept(UUID seanceId, UUID clubId) {
        String mutation = """
                mutation($seanceId: UUID!, $clubId: UUID!) {
                  acceptSeance(seanceId: $seanceId, clubId: $clubId) {
                    %s
                  }
                }
                """.formatted(SEANCE_FRAGMENT);
        try {
            JsonNode data = planningGraphQlClient.execute(mutation, Map.of(
                    "seanceId", seanceId.toString(),
                    "clubId", clubId.toString()
            ));
            return extractDecision(data, "acceptSeance");
        } catch (GraphQlClientException ex) {
            throw translatePlanningException(ex, "Impossible d'accepter la seance %s".formatted(seanceId));
        }
    }

    public SeanceDto refuse(UUID seanceId, UUID clubId, String reason) {
        String mutation = """
                mutation($seanceId: UUID!, $clubId: UUID!, $reason: String!) {
                  refuseSeance(seanceId: $seanceId, clubId: $clubId, reason: $reason) {
                    %s
                  }
                }
                """.formatted(SEANCE_FRAGMENT);
        try {
            JsonNode data = planningGraphQlClient.execute(mutation, Map.of(
                    "seanceId", seanceId.toString(),
                    "clubId", clubId.toString(),
                    "reason", reason
            ));
            return extractDecision(data, "refuseSeance");
        } catch (GraphQlClientException ex) {
            throw translatePlanningException(ex, "Impossible de refuser la seance %s".formatted(seanceId));
        }
    }

    private SeanceDto extractDecision(JsonNode data, String field) {
        JsonNode node = data.get(field);
        if (node == null || node.isNull()) {
            throw new ExternalServiceException("Reponse invalide lors de l'appel %s".formatted(field));
        }
        return mapSeance(node);
    }

    private SeanceDto mapSeance(JsonNode node) {
        String statusText = node.get("status").asText();
        SeanceStatus status;
        try {
            status = SeanceStatus.valueOf(statusText);
        } catch (IllegalArgumentException ex) {
            throw new ExternalServiceException("Statut de seance inconnu: " + statusText, ex);
        }
        return new SeanceDto(
                UUID.fromString(node.get("id").asText()),
                UUID.fromString(node.get("idClub").asText()),
                status,
                node.hasNonNull("refusalReason") ? node.get("refusalReason").asText() : null
        );
    }

    private RuntimeException translatePlanningException(GraphQlClientException ex, String fallbackMessage) {
        if (ex.hasClassification("NOT_FOUND") || ex.messageContainsIgnoreCase("not found")) {
            return new ResourceNotFoundException(ex.firstMessageOr(fallbackMessage));
        }
        if (ex.hasClassification("UNAUTHORIZED")) {
            return new ForbiddenOperationException(ex.firstMessageOr(fallbackMessage));
        }
        if (ex.hasClassification("BAD_REQUEST")) {
            return new BadRequestException(ex.firstMessageOr(fallbackMessage));
        }
        return ex;
    }

    public record SeanceDto(UUID id, UUID clubId, SeanceStatus status, String refusalReason) {
    }
}
