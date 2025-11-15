package fr.monclub.club_service.club.service;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.config.RabbitMQ.Publisher.ClubDisabledPublisher;
import fr.monclub.club_service.config.RabbitMQ.Publisher.ClubReactivedEventPublisher;
import fr.monclub.club_service.core.exception.ClubAccessDeniedException;
import fr.monclub.club_service.core.exception.ClubNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReactivateClubService {

    private final ClubCatalog clubCatalog;
    private final ClubReactivedEventPublisher clubReactivedEventPublisher;

    public boolean apply(UUID id, UUID ownerId) {
        Club existing = clubCatalog.getClubById(id);

        if (existing == null) {
            throw new ClubNotFoundException(id);
        }

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ClubAccessDeniedException("Vous n'êtes pas autorisé à modifier ce club.");
        }

        existing.setDeletedAt(null);
        existing.setActive(true);
        clubReactivedEventPublisher.publishReactivated(id, true);
        clubCatalog.save(existing);
        return true;
    }

}
