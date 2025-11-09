package fr.monclub.bff.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "monclub")
public record ExternalServicesProperties(ServiceProperties planning,
                                         ServiceProperties club,
                                         ServiceProperties membership) {

    public ExternalServicesProperties {
        if (planning == null) {
            throw new IllegalStateException("monclub.planning configuration is missing");
        }
        if (club == null) {
            throw new IllegalStateException("monclub.club configuration is missing");
        }
        if (membership == null) {
            throw new IllegalStateException("monclub.membership configuration is missing");
        }
    }

    public record ServiceProperties(String graphqlUrl) {
        public ServiceProperties {
            if (graphqlUrl == null || graphqlUrl.isBlank()) {
                throw new IllegalArgumentException("GraphQL URL must be provided");
            }
        }
    }
}
