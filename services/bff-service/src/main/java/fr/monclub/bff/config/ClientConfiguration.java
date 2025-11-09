package fr.monclub.bff.config;

import fr.monclub.bff.client.GraphQlClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {

    @Bean(name = "planningGraphQlClient")
    public GraphQlClient planningGraphQlClient(WebClient.Builder builder,
                                               ExternalServicesProperties properties) {
        return new GraphQlClient(builder
                .baseUrl(properties.planning().graphqlUrl())
                .build());
    }

    @Bean(name = "clubGraphQlClient")
    public GraphQlClient clubGraphQlClient(WebClient.Builder builder,
                                           ExternalServicesProperties properties) {
        return new GraphQlClient(builder
                .baseUrl(properties.club().graphqlUrl())
                .build());
    }

    @Bean(name = "membershipGraphQlClient")
    public GraphQlClient membershipGraphQlClient(WebClient.Builder builder,
                                                 ExternalServicesProperties properties) {
        System.out.println("my urrrrl " + properties.membership().graphqlUrl());
        return new GraphQlClient(builder
                .baseUrl(properties.membership().graphqlUrl())
                .build());
    }
}
