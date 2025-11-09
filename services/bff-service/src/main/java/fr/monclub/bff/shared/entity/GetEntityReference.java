package fr.monclub.bff.shared.entity;

import java.util.UUID;
import java.util.function.Function;

@FunctionalInterface
public interface GetEntityReference<T> extends Function<UUID, T> {
    T apply(UUID id);
}