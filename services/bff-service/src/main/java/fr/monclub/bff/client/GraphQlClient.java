package fr.monclub.bff.client;

import com.fasterxml.jackson.databind.JsonNode;
import fr.monclub.bff.exception.ExternalServiceException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

public class GraphQlClient {

    private final WebClient webClient;

    public GraphQlClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public JsonNode execute(String document, Map<String, Object> variables) {
        try {
            JsonNode response = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new GraphQlRequest(document, variables))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (response == null) {
                throw new ExternalServiceException("GraphQL response is empty");
            }

            JsonNode errors = response.get("errors");
            if (errors != null && errors.isArray() && errors.size() > 0) {
                throw new ExternalServiceException("GraphQL errors: " + errors);
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
