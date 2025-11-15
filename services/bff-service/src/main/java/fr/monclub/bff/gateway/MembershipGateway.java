package fr.monclub.bff.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import fr.monclub.bff.client.GraphQlClient;
import fr.monclub.bff.client.GraphQlClientException;
import fr.monclub.bff.dto.UserInput;
import fr.monclub.bff.exception.ForbiddenOperationException;
import fr.monclub.bff.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import fr.monclub.bff.gateway.ClubGateway;

@Component
@RequiredArgsConstructor
public class MembershipGateway {

    @Qualifier("membershipGraphQlClient")
    private final GraphQlClient membershipGraphQlClient;

    public MemberDto subscribeToClub(UUID clubId, UserInput newMember, ClubGateway.ClubDto club) {
        System.out.println("dans bff subscribetoclub va appeler le service");
        System.out.println("clubId: " + clubId);
        System.out.println("newMember: " + newMember.getEmail());
        System.out.println("newMember: " + newMember.getFirstName());
        System.out.println("newMember: " + newMember.getLastName());
        System.out.println("newMember: " + newMember.getBirthDate());
        System.out.println("newMember: " + newMember.getPhoneNumber());

        String mutation = """
        mutation($clubId: ID!, $newMember: UserInput!, $club: ClubInput!) {
          subscribeToClub(clubId: $clubId, newMember: $newMember, club: $club) {
                clubId
                userId
                firstName
                lastName
                birthDate
                email
                phoneNumber
                isActif
                dateSubscription
                himself
                club {
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
            }
            """;
        try {
            // Build GraphQL variables map matching club-membership-service schema
            Map<String, Object> variables = new java.util.HashMap<>();
            variables.put("clubId", clubId != null ? clubId.toString() : null);
            variables.put("newMember", newMember != null ? newMember.toGraphqlMap() : null);

            // Build full club snapshot input
            Map<String, Object> clubInput = new java.util.HashMap<>();
            if (club != null) {
                clubInput.put("id", club.id() != null ? club.id().toString() : null);
                clubInput.put("ownerId", club.ownerId() != null ? club.ownerId().toString() : null);
                clubInput.put("name", club.name());
                clubInput.put("typeId", club.typeId() != null ? club.typeId().toString() : null);
                clubInput.put("typeName", club.typeName());
                clubInput.put("city", club.city());
                clubInput.put("address", club.address());
                clubInput.put("country", club.country());
                clubInput.put("postalCode", club.postalCode());
                clubInput.put("phoneNumber", club.phoneNumber());
                clubInput.put("email", club.email());
                if (club.latitude() != null) clubInput.put("latitude", club.latitude());
                if (club.longitude() != null) clubInput.put("longitude", club.longitude());
                // Timestamps in ISO-8601 strings

            }
        variables.put("club", clubInput);

        // Debug: log snapshot being sent to membership service
        System.out.println("BFF -> membership subscribeToClub variables: clubId=" + variables.get("clubId")
            + ", newMember.userId=" + (newMember != null ? newMember.getUserId() : null)
            + ", club.name=" + clubInput.get("name")
            + ", club.ownerId=" + clubInput.get("ownerId")
            + ", club.typeId=" + clubInput.get("typeId")
            + ", club.typeName=" + clubInput.get("typeName")
            + ", club.email=" + clubInput.get("email")
            + ", club.lat=" + clubInput.get("latitude")
            + ", club.lon=" + clubInput.get("longitude")
            + ", club.createdAt=" + clubInput.get("createdAt")
        );

            JsonNode data = membershipGraphQlClient.execute(mutation, variables);
            System.out.println("GraphQL mutation executed, received data: " + data);
            if (data == null) return null;

            JsonNode n = data.get("subscribeToClub");
            if (n == null || n.isNull()) return null;
            return new MemberDto(
                    readUuid(n, "clubId"),
                    readUuid(n, "userId"),
                    n.hasNonNull("firstName") ? n.get("firstName").asText() : null,
                    n.hasNonNull("lastName") ? n.get("lastName").asText() : null,
                    readOdt(n, "birthDate"),
                    n.hasNonNull("email") ? n.get("email").asText() : null,
                    n.hasNonNull("phoneNumber") ? n.get("phoneNumber").asText() : null,
                    n.hasNonNull("isActif") ? n.get("isActif").asBoolean() : null,
                    readOdt(n, "dateSubscription"),
                    n.hasNonNull("himself") ? n.get("himself").asBoolean() : null
            );

        } catch (GraphQlClientException ex) {
            if (ex.hasClassification("UNAUTHORIZED")) {
                throw new ForbiddenOperationException(ex.firstMessageOr("Accès refusé pour l'inscription au club %s".formatted(clubId)));
            }
            if (ex.hasClassification("NOT_FOUND")) {
                throw new ResourceNotFoundException(ex.firstMessageOr("Utilisateur ou club introuvable"));
            }
            throw ex;
        }
    }

    public int forcedUnsubscribe(UUID clubId, UUID memberId) {
        String mutation = """
            mutation($clubId: ID!, $memberId: ID!) {
              forcedUnsubscribe(clubId: $clubId, memberId: $memberId)
            }
            """;
        try {
            Map<String, Object> variables = Map.of(
                    "clubId", clubId.toString(),
                    "memberId", memberId.toString()
            );
            JsonNode data = membershipGraphQlClient.execute(mutation, variables);
            JsonNode value = data != null ? data.get("forcedUnsubscribe") : null;
            return value != null && value.isInt() ? value.asInt() : 0;
        } catch (GraphQlClientException ex) {
            if (ex.hasClassification("UNAUTHORIZED")) {
                throw new ForbiddenOperationException(ex.firstMessageOr("Accès refusé pour la désinscription forcée"));
            }
            if (ex.hasClassification("NOT_FOUND")) {
                throw new ResourceNotFoundException(ex.firstMessageOr("Club ou membre introuvable"));
            }
            throw ex;
        }
    }

    public List<MemberWithClubDto> clubMembersByUserId(UUID userId) {
        String query = """
            query($userId: ID!) {
              clubMembersByUserId(userId: $userId) {
                clubId
                userId
                firstName
                lastName
                birthDate
                email
                phoneNumber
                isActif
                dateSubscription
                dateUnsubscription
                himself
                club {
                  id
                  ownerId
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
                  createdAt
                  updatedAt
                  deletedAt
                }
              }
            }
            """;
        try {
            Map<String, Object> variables = Map.of("userId", userId.toString());
            JsonNode data = membershipGraphQlClient.execute(query, variables);
            List<MemberWithClubDto> result = new ArrayList<>();
            JsonNode arr = data != null ? data.get("clubMembersByUserId") : null;
            if (arr != null && arr.isArray()) {
                for (JsonNode n : arr) {
                    ClubGateway.ClubDto club = null;
                    JsonNode clubNode = n.get("club");
                    if (clubNode != null && !clubNode.isNull()) {
                        club = new ClubGateway.ClubDto(
                            readUuid(clubNode, "id"),
                            readUuid(clubNode, "ownerId"),
                            clubNode.hasNonNull("name") ? clubNode.get("name").asText() : null,
                            readUuid(clubNode, "typeId"),
                            clubNode.hasNonNull("typeName") ? clubNode.get("typeName").asText() : null,
                            clubNode.hasNonNull("city") ? clubNode.get("city").asText() : null,
                            clubNode.hasNonNull("address") ? clubNode.get("address").asText() : null,
                            clubNode.hasNonNull("country") ? clubNode.get("country").asText() : null,
                            clubNode.hasNonNull("postalCode") ? clubNode.get("postalCode").asText() : null,
                            clubNode.hasNonNull("phoneNumber") ? clubNode.get("phoneNumber").asText() : null,
                            clubNode.hasNonNull("email") ? clubNode.get("email").asText() : null,
                            readFloat(clubNode, "latitude"),
                            readFloat(clubNode, "longitude"),
                            readOdt(clubNode, "createdAt"),
                            readOdt(clubNode, "updatedAt"),
                            readOdt(clubNode, "deletedAt"),
                                false
                        );
                    }
                    result.add(new MemberWithClubDto(
                            readUuid(n, "clubId"),
                            readUuid(n, "userId"),
                            n.hasNonNull("firstName") ? n.get("firstName").asText() : null,
                            n.hasNonNull("lastName") ? n.get("lastName").asText() : null,
                            readOdt(n, "birthDate"),
                            n.hasNonNull("email") ? n.get("email").asText() : null,
                            n.hasNonNull("phoneNumber") ? n.get("phoneNumber").asText() : null,
                            n.hasNonNull("isActif") ? n.get("isActif").asBoolean() : null,
                            readOdt(n, "dateSubscription"),
                            readOdt(n, "dateUnsubscription"),
                            n.hasNonNull("himself") ? n.get("himself").asBoolean() : null,
                            club
                    ));
                }
            }
            return result;
        } catch (GraphQlClientException ex) {
            if (ex.hasClassification("UNAUTHORIZED")) {
                throw new ForbiddenOperationException(ex.firstMessageOr("Accès refusé pour la consultation des adhésions"));
            }
            throw ex;
        }
    }

    public List<MemberWithClubDto> clubMembersByClubId(UUID clubId) {
        String query = """
            query($clubId: ID!) {
              AllClubMembersByClubId(clubId: $clubId) {
                userId
                clubId
                firstName
                lastName
                birthDate
                email
                phoneNumber
                isActif
                dateSubscription
                dateUnsubscription
                himself
                club {
                  id
                  ownerId
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
                  createdAt
                  updatedAt
                  deletedAt
                }
              }
            }
            """;
        try {
            Map<String, Object> variables = Map.of("clubId", clubId.toString());
            JsonNode data = membershipGraphQlClient.execute(query, variables);
            List<MemberWithClubDto> result = new ArrayList<>();
            JsonNode arr = data != null ? data.get("AllClubMembersByClubId") : null;
            if (arr != null && arr.isArray()) {
                for (JsonNode n : arr) {
                    ClubGateway.ClubDto club = null;
                    JsonNode clubNode = n.get("club");
                    if (clubNode != null && !clubNode.isNull()) {
                        club = new ClubGateway.ClubDto(
                            readUuid(clubNode, "id"),
                            readUuid(clubNode, "ownerId"),
                            clubNode.hasNonNull("name") ? clubNode.get("name").asText() : null,
                            readUuid(clubNode, "typeId"),
                            clubNode.hasNonNull("typeName") ? clubNode.get("typeName").asText() : null,
                            clubNode.hasNonNull("city") ? clubNode.get("city").asText() : null,
                            clubNode.hasNonNull("address") ? clubNode.get("address").asText() : null,
                            clubNode.hasNonNull("country") ? clubNode.get("country").asText() : null,
                            clubNode.hasNonNull("postalCode") ? clubNode.get("postalCode").asText() : null,
                            clubNode.hasNonNull("phoneNumber") ? clubNode.get("phoneNumber").asText() : null,
                            clubNode.hasNonNull("email") ? clubNode.get("email").asText() : null,
                            readFloat(clubNode, "latitude"),
                            readFloat(clubNode, "longitude"),
                            readOdt(clubNode, "createdAt"),
                            readOdt(clubNode, "updatedAt"),
                            readOdt(clubNode, "deletedAt"),
                                false
                        );
                    }
                    result.add(new MemberWithClubDto(
                            readUuid(n, "clubId"),
                            readUuid(n, "userId"),
                            n.hasNonNull("firstName") ? n.get("firstName").asText() : null,
                            n.hasNonNull("lastName") ? n.get("lastName").asText() : null,
                            readOdt(n, "birthDate"),
                            n.hasNonNull("email") ? n.get("email").asText() : null,
                            n.hasNonNull("phoneNumber") ? n.get("phoneNumber").asText() : null,
                            n.hasNonNull("isActif") ? n.get("isActif").asBoolean() : null,
                            readOdt(n, "dateSubscription"),
                            readOdt(n, "dateUnsubscription"),
                            n.hasNonNull("himself") ? n.get("himself").asBoolean() : null,
                            club
                    ));
                }
            }
            return result;
        } catch (GraphQlClientException ex) {
            if (ex.hasClassification("UNAUTHORIZED")) {
                throw new ForbiddenOperationException(ex.firstMessageOr("Accès refusé pour la consultation des membres du club"));
            }
            throw ex;
        }
    }

    private static UUID readUuid(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v != null && !v.isNull() && !v.asText().isBlank()) ? UUID.fromString(v.asText()) : null;
    }

    private static OffsetDateTime readOdt(JsonNode node, String field) {
        JsonNode v = node.get(field);
        try {
            return (v != null && !v.isNull() && !v.asText().isBlank()) ? OffsetDateTime.parse(v.asText()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static Float readFloat(JsonNode node, String field) {
        JsonNode v = node.get(field);
        if (v == null || v.isNull()) {
            return null;
        }
        if (v.isNumber()) {
            return v.floatValue();
        }
        try {
            return Float.parseFloat(v.asText());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean unsubscribeFromClub(UUID clubId, UUID memberId) {
        String mutation = """
            mutation($clubId: ID!, $memberId: ID!) {
              unsubscribeFromClub(clubId: $clubId, memberId: $memberId)
            }
            """;
        try {
            Map<String, Object> variables = new java.util.HashMap<>();
            variables.put("clubId", clubId != null ? clubId.toString() : null);
            variables.put("memberId", memberId != null ? memberId.toString() : null);
            JsonNode data = membershipGraphQlClient.execute(mutation, variables);
            JsonNode node = data != null ? data.get("unsubscribeFromClub") : null;
            int affected = node != null ? node.asInt(0) : 0;
            return affected > 0;
        } catch (GraphQlClientException ex) {
            if (ex.hasClassification("UNAUTHORIZED")) {
                throw new ForbiddenOperationException(ex.firstMessageOr("Acces refuse pour la desinscription du club"));
            }
            if (ex.hasClassification("NOT_FOUND")) {
                throw new ResourceNotFoundException(ex.firstMessageOr("Adhesion introuvable"));
            }
            throw ex;
        }
    }

    public record MemberDto(
            UUID clubId,
            UUID userId,
            String firstName,
            String lastName,
            OffsetDateTime birthDate,
            String email,
            String phoneNumber,
            Boolean isActif,
            OffsetDateTime dateSubscription,
            Boolean himself
    ) {}

    public record MemberWithClubDto(
            UUID clubId,
            UUID userId,
            String firstName,
            String lastName,
            OffsetDateTime birthDate,
            String email,
            String phoneNumber,
            Boolean isActif,
            OffsetDateTime dateSubscription,
            OffsetDateTime dateUnsubscription,
            Boolean himself,
            ClubGateway.ClubDto club
    ) {}
}
