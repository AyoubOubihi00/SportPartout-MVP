package fr.monclub.club_membership_service.shared.entity;

@FunctionalInterface
public interface GetEntity<T> {
    T apply();
}