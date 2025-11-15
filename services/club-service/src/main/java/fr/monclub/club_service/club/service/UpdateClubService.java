package fr.monclub.club_service.club.service;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.config.RabbitMQ.Publisher.ClubEventPublisher;
import fr.monclub.club_service.core.exception.ClubAccessDeniedException;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.validators.UpdateClubValidator;
import fr.monclub.club_service.core.exception.ClubNotFoundException;
import lombok.AllArgsConstructor;
import fr.monclub.club_service.club.mapper.UpdateClubInputToClubMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UpdateClubService {

    private final ClubCatalog clubCatalog;
    private final UpdateClubInputToClubMapper mapper;
    private final UpdateClubValidator validator;
    private final ClubEventPublisher clubEventPublisher;

    public Club apply(UUID id, UpdateClubInput input, UUID ownerId) {
        Club existing = clubCatalog.getClubById(id);

        if (existing == null) {
            throw new ClubNotFoundException(id);
        }

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ClubAccessDeniedException("Vous n'êtes pas autorisé à modifier ce club.");
        }

        validator.apply(input);

        System.out.println("Publication de l'événement ClubUpdatedEvent pour le club " + id);
        clubEventPublisher.publishClubUpdated(id,input);
        System.out.println("Événement ClubUpdatedEvent publié pour le club " + id);
        return clubCatalog.save(mapper.updateClub(input, existing));

    }

}
