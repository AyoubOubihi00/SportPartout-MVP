package fr.monclub.club_service.club.service;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.mapper.UpdateClubInputToClubMapper;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.validators.UpdateClubValidator;
import fr.monclub.club_service.config.RabbitMQ.Events.ClubDisabledEvent;
import fr.monclub.club_service.config.RabbitMQ.Publisher.ClubDisabledPublisher;
import fr.monclub.club_service.core.exception.ClubAccessDeniedException;
import fr.monclub.club_service.core.exception.ClubNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DeleteClubService {

    private final ClubCatalog clubCatalog;
    private final ClubDisabledPublisher clubDisabledPublisher;

    public boolean apply(UUID id, UUID ownerId) {
        Club existing = clubCatalog.getClubById(id);

        if (existing == null) {
            throw new ClubNotFoundException(id);
        }

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ClubAccessDeniedException("Vous n'êtes pas autorisé à modifier ce club.");
        }

        OffsetDateTime now = OffsetDateTime.now();
        existing.setDeletedAt(now);
        existing.setActive(false);
        clubDisabledPublisher.publishClubDisabled(id,now,false);

        clubCatalog.save(existing);
        return true;
    }

}
