package fr.monclub.club_membership_service.shared.entity;

import java.util.UUID;
import java.util.function.Function;

@FunctionalInterface
public interface GetEntityReference<T> extends Function<UUID, T> {
    T apply(UUID id);
}