package fr.monclub.club_service.club.catalog;

import fr.monclub.club_service.club.model.Club;

import java.util.List;
import java.util.UUID;

public interface ClubCatalog {

    List<Club> getAllClubs();

    Club getClubById(UUID id);

    List<Club> getClubsByOwnerId(UUID ownerId);

    Club save(Club club);

    Club saveAndFlush(Club club);

    void flush();
}
