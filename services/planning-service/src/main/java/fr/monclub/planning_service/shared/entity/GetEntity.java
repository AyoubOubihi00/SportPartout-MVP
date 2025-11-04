package fr.monclub.planning_service.shared.entity;

@FunctionalInterface
public interface GetEntity<T> {
    T apply();
}