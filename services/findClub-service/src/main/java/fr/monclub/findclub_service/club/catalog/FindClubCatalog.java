package fr.monclub.findclub_service.club.catalog;

import fr.monclub.findclub_service.club.model.Club;
import fr.monclub.findclub_service.club.model.FindClubWithDistance;
import java.util.List;

public interface FindClubCatalog {

    List<Club> getAllClubs();

    List<Club> getClubsByCity(String city);

    List<Club> getClubsByCityAndPostalCode(String city, String postalCode);

    List<FindClubWithDistance> getClubsNear(double latitude, double longitude, double radiusKm);

    Club save(Club club);

    Club saveAndFlush(Club club);
}
