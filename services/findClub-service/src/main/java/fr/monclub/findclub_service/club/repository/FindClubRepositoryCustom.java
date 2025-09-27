package fr.monclub.findclub_service.club.repository;

import fr.monclub.findclub_service.club.model.FindClubWithDistance;

import java.util.List;
import java.util.UUID;

public interface FindClubRepositoryCustom {

    List<FindClubWithDistance> findWithinRadius(double latitude, double longitude, double radiusKm);

    List<FindClubWithDistance> findWithinRadius(double latitude, double longitude, double radiusKm, UUID typeIdSport, String typeNameSport);
}
