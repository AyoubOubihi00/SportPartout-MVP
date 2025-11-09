package fr.monclub.bff.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import fr.monclub.bff.client.GraphQlClient;
import fr.monclub.bff.client.GraphQlClientException;
import fr.monclub.bff.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClubGateway {

    @Qualifier("clubGraphQlClient")
    private final GraphQlClient clubGraphQlClient;

    public ClubDto getClub(UUID id) {
        System.out.println("ClubGateway: getClub called with id=" + id);
                String query = """
                        query($id: UUID!) {
              clubById(id: $id) {
                id
                name
                typeId
                typeName
                city
                address
                country
                postalCode
                phoneNumber
                email
                latitude
                longitude
                ownerId
                createdAt
                updatedAt
                deletedAt
              }
            }
            """;
        try {
            JsonNode data = clubGraphQlClient.execute(query, Map.of("id", id.toString()));
            JsonNode n = data != null ? data.get("clubById") : null;
            if (n == null || n.isNull()) {
                throw new ResourceNotFoundException("Club introuvable");
            }
            return new ClubDto(
                    readUuid(n, "id"),
                    readUuid(n, "ownerId"),
                    n.path("name").asText(null),
                    readUuid(n, "typeId"),
                    n.path("typeName").asText(null),
                    n.path("city").asText(null),
                    n.path("address").asText(null),
                    n.path("country").asText(null),
                    n.path("postalCode").asText(null),
                    n.path("phoneNumber").asText(null),
                    n.path("email").asText(null),
                    n.hasNonNull("latitude") ? n.get("latitude").floatValue() : null,
                    n.hasNonNull("longitude") ? n.get("longitude").floatValue() : null
            );
        } catch (GraphQlClientException ex) {
            // club-service currently classifies missing clubs as DataFetchingException; handle both
            if (ex.hasClassification("NOT_FOUND") || ex.hasClassification("DataFetchingException")) {
                throw new ResourceNotFoundException(ex.firstMessageOr("Club introuvable"));
            }
            throw ex;
        }
    }

    public List<ClubDto> getClubsByOwnerId(UUID ownerId) {
        String query = """
                query($id: UUID!) {
          clubsByOwnerId(id: $id) {
            id
            name
            typeId
            typeName
            city
            address
            country
            postalCode
            phoneNumber
            email
            latitude
            longitude
          }
        }
        """;
        try {
            JsonNode data = clubGraphQlClient.execute(query, Map.of("id", ownerId.toString()));
            JsonNode arr = data != null ? data.get("clubsByOwnerId") : null;
            List<ClubDto> result = new ArrayList<>();
            if (arr != null && arr.isArray()) {
                for (JsonNode n : arr) {
                    result.add(new ClubDto(
                            readUuid(n, "id"),
                            ownerId,
                            n.hasNonNull("name") ? n.get("name").asText() : null,
                            readUuid(n, "typeId"),
                            n.hasNonNull("typeName") ? n.get("typeName").asText() : null,
                            n.hasNonNull("city") ? n.get("city").asText() : null,
                            n.hasNonNull("address") ? n.get("address").asText() : null,
                            n.hasNonNull("country") ? n.get("country").asText() : null,
                            n.hasNonNull("postalCode") ? n.get("postalCode").asText() : null,
                            n.hasNonNull("phoneNumber") ? n.get("phoneNumber").asText() : null,
                            n.hasNonNull("email") ? n.get("email").asText() : null,
                            n.hasNonNull("latitude") ? (float) n.get("latitude").asDouble() : null,
                            n.hasNonNull("longitude") ? (float) n.get("longitude").asDouble() : null
                    ));
                }
            }
            return result;
        } catch (GraphQlClientException ex) {
            if (ex.hasClassification("NOT_FOUND") || ex.hasClassification("DataFetchingException")) {
                return List.of();
            }
            throw ex;
        }
    }

    private static UUID readUuid(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v != null && !v.isNull() && !v.asText().isBlank()) ? UUID.fromString(v.asText()) : null;
    }

    public record ClubDto(
            UUID id,
            UUID ownerId,
            String name,
            UUID typeId,
            String typeName,
            String city,
            String address,
            String country,
            String postalCode,
            String phoneNumber,
            String email,
            Float latitude,
            Float longitude
    ) {}
}
