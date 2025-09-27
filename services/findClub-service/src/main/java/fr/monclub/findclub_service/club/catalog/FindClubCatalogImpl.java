package fr.monclub.findclub_service.club.catalog;

import fr.monclub.findclub_service.club.model.Club;
import fr.monclub.findclub_service.club.model.FindClubWithDistance;
import fr.monclub.findclub_service.club.repository.FindClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FindClubCatalogImpl implements FindClubCatalog {

    private final FindClubRepository findClubRepository;

    @Override
    public List<Club> getAllClubs() {
        return findClubRepository.findAll();
    }

    @Override
    public List<Club> getClubsByCity(String city) {
        return findByCityAndPostalCode(normalizeCity(city), null);
    }

    @Override
    public List<Club> getClubsByCityAndPostalCode(String city, String postalCode) {
        return findByCityAndPostalCode(normalizeCity(city), normalizePostalCode(postalCode));
    }

    @Override
    public List<FindClubWithDistance> getClubsNear(double latitude, double longitude, double radiusKm) {
        return findClubRepository.findWithinRadius(latitude, longitude, Math.max(radiusKm, 0d));
    }

    @Override
    public Club save(Club club) {
        return findClubRepository.save(club);
    }

    @Override
    public Club saveAndFlush(Club club) {
        return findClubRepository.saveAndFlush(club);
    }

    private List<Club> findByCityAndPostalCode(String normalizedCity, String normalizedPostalCode) {
        if (normalizedCity == null && normalizedPostalCode == null) {
            return List.of();
        }

        List<Club> clubs = findClubRepository.findAllByCityAndPostalCode(normalizedCity, normalizedPostalCode);

        if (clubs.isEmpty() && normalizedCity != null && normalizedPostalCode != null) {
            clubs = findClubRepository.findAllByCityAndPostalCode(normalizedCity, null);
        }

        return clubs;
    }

    private String normalizeCity(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }

    private String normalizePostalCode(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }
}