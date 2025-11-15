package fr.monclub.bff.dto;

import fr.monclub.bff.gateway.ClubGateway;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SubscribeResult(
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
