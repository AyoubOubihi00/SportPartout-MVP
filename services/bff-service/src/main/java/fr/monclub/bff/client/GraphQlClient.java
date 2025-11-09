package fr.monclub.bff.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.monclub.bff.exception.ExternalServiceException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Map;

public class GraphQlClient {

    private final WebClient webClient;

    public GraphQlClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public JsonNode execute(String document, Map<String, Object> variables) {
        try {
            // Forward caller Authorization header (raw JWT) as-is if present
            String authorizationHeader;
            String claimsHeader;
            var attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes servletAttrs) {
                var req = servletAttrs.getRequest();
                authorizationHeader = req.getHeader("Authorization");
                // Also forward X-JWT-Claims when available (added by Envoy), harmless downstream
                claimsHeader = req.getHeader("X-JWT-Claims");
            } else {
                claimsHeader = null;
                authorizationHeader = null;
            }

            JsonNode response = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(h -> {
                        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
                            h.set("Authorization", authorizationHeader);
                        }
                        if (claimsHeader != null && !claimsHeader.isBlank()) {
                            h.set("X-JWT-Claims", claimsHeader);
                        }
                    })
                    .bodyValue(new GraphQlRequest(document, variables))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (response == null) {
                throw new ExternalServiceException("GraphQL response is empty");
            }

            System.out.println("GraphQL response: " + response.toString());
            JsonNode errorsNode = response.get("errors");
            if (errorsNode != null && errorsNode.isArray() && errorsNode.size() > 0) {
                System.out.println("GraphQL errors: " + errorsNode.toString());
                throw new GraphQlClientException((ArrayNode) errorsNode);
            }

            JsonNode data = response.get("data");
            if (data == null || data.isNull()) {
                throw new ExternalServiceException("GraphQL response does not contain data");
            }
            return data;
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalServiceException("GraphQL call failed: " + e.getMessage(), e);
        }
    }

    private record GraphQlRequest(String query, Map<String, Object> variables) {
    }
}
