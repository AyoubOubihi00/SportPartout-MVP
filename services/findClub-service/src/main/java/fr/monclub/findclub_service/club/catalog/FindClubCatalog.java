package fr.monclub.findclub_service.club.catalog;

import fr.monclub.findclub_service.RabbitMQ.listener.ClubDisabledEvent;
import fr.monclub.findclub_service.RabbitMQ.listener.ClubReactivedEvent;
import fr.monclub.findclub_service.club.model.Club;
import fr.monclub.findclub_service.club.model.FindClubWithDistance;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface FindClubCatalog {

    List<Club> getAllClubs();

    Club getClubByClubId(UUID clubId);

    List<Club> getClubsByCity(String city);

    List<Club> getClubsByCityAndPostalCode(String city, String postalCode);

    List<FindClubWithDistance> getClubsNear(double latitude, double longitude, double radiusKm);

    Club save(Club club);

    Club saveAndFlush(Club club);
    public void updateClubInfo(UUID clubId, String name, UUID typeId, String typeName,
                               String city, String address, String country, String postalCode,
                               String phoneNumber, String email, Double latitude, Double longitude,
                               OffsetDateTime updatedAt);

    public void disableClub(ClubDisabledEvent clubDisabledEvent);
    public void reactiveClub(ClubReactivedEvent clubReactivedEvent);


}
