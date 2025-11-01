package fr.monclub.bff.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import fr.monclub.bff.client.GraphQlClient;
import fr.monclub.bff.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClubGateway {

    @Qualifier("clubGraphQlClient")
    private final GraphQlClient clubGraphQlClient;

    public ClubDto getClub(UUID clubId) {
        String query = """
                query($id: UUID!) {
                  clubById(id: $id) {
                    id
                    ownerId
                    name
                  }
                }
                """;
        JsonNode data = clubGraphQlClient.execute(query, Map.of("id", clubId.toString()));
        JsonNode clubNode = data.get("clubById");
        if (clubNode == null || clubNode.isNull()) {
            throw new ResourceNotFoundException("Club %s introuvable".formatted(clubId));
        }
        return new ClubDto(
                UUID.fromString(clubNode.get("id").asText()),
                UUID.fromString(clubNode.get("ownerId").asText()),
                clubNode.get("name").asText()
        );
    }

    public record ClubDto(UUID id, UUID ownerId, String name) {
    }
}
