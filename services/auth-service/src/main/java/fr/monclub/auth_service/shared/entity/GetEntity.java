package fr.monclub.auth_service.shared.entity;

@FunctionalInterface
public interface GetEntity<T> {
    T apply();
}