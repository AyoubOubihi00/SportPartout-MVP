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
import java.time.OffsetDateTime;

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
                    n.hasNonNull("longitude") ? n.get("longitude").floatValue() : null,
                    readOdt(n, "createdAt"),
                    readOdt(n, "updatedAt"),
                    readOdt(n, "deletedAt")
                );
        } catch (GraphQlClientException ex) {
            // club-service currently classifies missing clubs as DataFetchingException; handle both
            if (ex.hasClassification("NOT_FOUND") || ex.hasClassification("DataFetchingException")) {
                throw new ResourceNotFoundException(ex.firstMessageOr("Club introuvable"));
            }
            throw ex;
        }
    }

    public ClubDto updateClub(UUID id, Map<String, Object> data, UUID ownerId) {
        // IMPORTANT: Le schema du club-service utilise le scalar UUID, pas ID.
        // L'erreur "Unknown type 'ID'" provenait de cette définition incorrecte.
        String mutation = """
            mutation($id: UUID!, $data: UpdateClubInput!, $ownerId: UUID!) {
              updateClub(id: $id, data: $data, ownerId: $ownerId) {
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
            Map<String, Object> variables = Map.of(
                    "id", id.toString(),
                    "data", data,
                    "ownerId", ownerId != null ? ownerId.toString() : null
            );
            JsonNode resp = clubGraphQlClient.execute(mutation, variables);
            JsonNode n = resp != null ? resp.get("updateClub") : null;
            if (n == null || n.isNull()) {
                throw new ResourceNotFoundException("Club introuvable après mise à jour");
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
                    n.hasNonNull("longitude") ? n.get("longitude").floatValue() : null,
                    readOdt(n, "createdAt"),
                    readOdt(n, "updatedAt"),
                    readOdt(n, "deletedAt")
            );
        } catch (GraphQlClientException ex) {
            if (ex.hasClassification("NOT_FOUND") || ex.hasClassification("DataFetchingException")) {
                throw new ResourceNotFoundException(ex.firstMessageOr("Club introuvable"));
            }
            throw ex;
        }
    }

    public boolean disableClub(UUID id, UUID ownerId) {
        String mutation = """
            mutation($id: UUID!, $ownerId: UUID!) {
              disableClub(id: $id, ownerId: $ownerId)
            }
            """;
        try {
            JsonNode data = clubGraphQlClient.execute(mutation, Map.of(
                    "id", id.toString(),
                    "ownerId", ownerId != null ? ownerId.toString() : null
            ));
            JsonNode v = data.get("disableClub");
            return v != null && v.asBoolean(false);
        } catch (GraphQlClientException ex) {
            if (ex.hasClassification("NOT_FOUND") || ex.hasClassification("DataFetchingException")) {
                return false; // ou lancer ResourceNotFoundException selon besoin métier
            }
            throw ex;
        }
    }

    public boolean reactivateClub(UUID id, UUID ownerId) {
        System.out.println("Reactivation du club avec id=" + id + " et ownerId=" + ownerId);
        String mutation = """
            mutation($id: UUID!, $ownerId: UUID!) {
              reactivateClub(id: $id, ownerId: $ownerId)
            }
            """;
        try {
            JsonNode data = clubGraphQlClient.execute(mutation, Map.of(
                    "id", id.toString(),
                    "ownerId", ownerId != null ? ownerId.toString() : null
            ));
            JsonNode v = data.get("reactivateClub");
            return v != null && v.asBoolean(false);
        } catch (GraphQlClientException ex) {
            if (ex.hasClassification("NOT_FOUND") || ex.hasClassification("DataFetchingException")) {
                return false;
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
                            n.hasNonNull("longitude") ? (float) n.get("longitude").asDouble() : null,
                            null,
                            null,
                            null
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
            Float longitude,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            OffsetDateTime deletedAt
    ) {}

    private static OffsetDateTime readOdt(JsonNode node, String field) {
        JsonNode v = node.get(field);
        try {
            return (v != null && !v.isNull() && !v.asText().isBlank()) ? OffsetDateTime.parse(v.asText()) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
