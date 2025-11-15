package fr.monclub.findclub_service.club.catalog;

import fr.monclub.findclub_service.RabbitMQ.listener.ClubDisabledEvent;
import fr.monclub.findclub_service.RabbitMQ.listener.ClubReactivedEvent;
import fr.monclub.findclub_service.club.model.Club;
import fr.monclub.findclub_service.club.model.FindClubWithDistance;
import fr.monclub.findclub_service.club.repository.FindClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

    public Club getClubByClubId(UUID clubId) {
        return findClubRepository.findById(clubId).orElse(null);
    }

    public void updateClubInfo(UUID clubId, String name, UUID typeId, String typeName,
                               String city, String address, String country, String postalCode,
                               String phoneNumber, String email, Double latitude, Double longitude,
                               OffsetDateTime updatedAt) {

        Club club = findClubRepository.findById(clubId).orElse(null);

        if (club == null) {
            System.out.println("Aucun club trouvé avec l'ID : " + clubId);
            return;
        }

        // Mise à jour des champs pour chaque membre

        club.setName(name);
        club.setTypeIdSport(typeId);
        club.setTypeNameSport(typeName);
        club.setCity(city);
        club.setAddress(address);
        club.setCountry(country);
        club.setPostalCode(postalCode);
        club.setPhoneNumber(phoneNumber);
        club.setEmail(email);
        club.setLatitude(latitude);
        club.setLongitude(longitude);
        club.setUpdatedAt(updatedAt);

        findClubRepository.save(club);

        System.out.println("Club mis à jour pour le club : " + clubId);
    }

    @Override
    public void disableClub(ClubDisabledEvent clubDisabledEvent) {
        Club club = findClubRepository.findById(clubDisabledEvent.getClubId()).orElse(null);

        if (club == null) {
            System.out.println("Aucun club trouvé avec l'ID : " );
            return;
        }

        club.setDeletedAt(clubDisabledEvent.getDisabledAt());
        club.setActive(clubDisabledEvent.isActive());

        findClubRepository.save(club);
        System.out.println("Club désactivé pour le club : " + clubDisabledEvent.getClubId());
    }

    @Override
    public void reactiveClub(ClubReactivedEvent clubReactivedEvent) {
        Club club = findClubRepository.findById(clubReactivedEvent.getClubId()).orElse(null);
        if (club == null) {
            System.out.println("Aucun club trouvé avec l'ID : " );
            return;
        }
        club.setActive(clubReactivedEvent.isActive());
        club.setDeletedAt(null);
        findClubRepository.save(club);
        System.out.println("Club réactivé pour le club : " + clubReactivedEvent.getClubId());
    }
}