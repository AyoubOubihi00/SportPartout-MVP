package fr.monclub.bff.shared.entity;

@FunctionalInterface
public interface GetEntity<T> {
    T apply();
}