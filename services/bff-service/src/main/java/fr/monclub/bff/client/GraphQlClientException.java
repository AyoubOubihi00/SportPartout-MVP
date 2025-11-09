package fr.monclub.bff.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.monclub.bff.exception.ExternalServiceException;

public class GraphQlClientException extends ExternalServiceException {

    private final ArrayNode errors;

    public GraphQlClientException(ArrayNode errors) {
        super("GraphQL errors: " + errors);
        this.errors = errors;
    }

    public ArrayNode errors() {
        return errors;
    }

    public boolean hasClassification(String classification) {
        if (classification == null || classification.isBlank()) {
            return false;
        }
        for (JsonNode error : errors) {
            JsonNode extensions = error.get("extensions");
            if (extensions == null) {
                continue;
            }
            JsonNode classificationNode = extensions.get("classification");
            if (classificationNode != null && classification.equalsIgnoreCase(classificationNode.asText())) {
                return true;
            }
        }
        return false;
    }

    public boolean messageContainsIgnoreCase(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String needle = text.toLowerCase();
        for (JsonNode error : errors) {
            JsonNode message = error.get("message");
            if (message != null && message.asText("").toLowerCase().contains(needle)) {
                return true;
            }
        }
        return false;
    }

    public String firstMessage() {
        if (errors.isEmpty()) {
            return "";
        }
        JsonNode first = errors.get(0);
        if (first == null) {
            return "";
        }
        JsonNode message = first.get("message");
        return message != null ? message.asText("") : "";
    }

    public String firstMessageOr(String fallback) {
        String message = firstMessage();
        return message == null || message.isBlank() ? fallback : message;
    }
}
