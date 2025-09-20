package fr.monclub.club_service.club.service;

import fr.monclub.club_service.club.catalog.ClubCatalog;
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

    public Club apply(UUID id, UpdateClubInput input, UUID ownerId) {
        Club existing = clubCatalog.getClubById(id);

        if (existing == null) {
            throw new ClubNotFoundException(id);
        }

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new ClubAccessDeniedException("Vous n'êtes pas autorisé à modifier ce club.");
        }

        validator.apply(input);


        return clubCatalog.save(mapper.updateClub(input, existing));
    }

}
