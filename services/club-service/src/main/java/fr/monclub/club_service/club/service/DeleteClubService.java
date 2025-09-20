package fr.monclub.club_service.club.service;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.mapper.UpdateClubInputToClubMapper;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.validators.UpdateClubValidator;
import fr.monclub.club_service.core.exception.ClubAccessDeniedException;
import fr.monclub.club_service.core.exception.ClubNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DeleteClubService {

    private final ClubCatalog clubCatalog;

    public boolean apply(UUID id, UUID ownerId) {
        Club existing = clubCatalog.getClubById(id);

        if (existing == null) {
            throw new ClubNotFoundException(id);
        }

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ClubAccessDeniedException("Vous n'êtes pas autorisé à modifier ce club.");
        }

        existing.setDeletedAt(new Date());
        existing.setActive(false);
        clubCatalog.save(existing);
        return true;
    }

}
