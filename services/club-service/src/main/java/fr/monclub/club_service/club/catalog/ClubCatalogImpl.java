package fr.monclub.club_service.club.catalog;

import fr.monclub.club_service.core.exception.ClubNotFoundException;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubCatalogImpl implements ClubCatalog {

    private final ClubRepository clubRepository;

    @Override
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    @Override
    public Club getClubById(UUID id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new ClubNotFoundException(id));
    }

    @Override
    public List<Club> getClubsByOwnerId(UUID ownerId) {
        return clubRepository.findAll().stream()
                .filter(club -> ownerId != null && ownerId.equals(club.getOwnerId()))
                .toList();
    }

    @Override
    public Club save(Club club) {
        return clubRepository.save(club);
    }

    @Override
    public Club saveAndFlush(Club club) {
        return clubRepository.saveAndFlush(club);
    }

    @Override
    public void flush() {
        clubRepository.flush();
    }

    @Override
    public List<Club> getAllClubsBySportTypeId(UUID id) {
        return clubRepository.findClubsByTypeId(id);
    }

    @Override
    public List<Club> saveAll(List<Club> clubs) {
        return  clubRepository.saveAll(clubs);
    }


}
