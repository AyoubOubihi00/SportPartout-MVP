package fr.monclub.club_service.club.service;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.validators.UpdateClubValidator;
import lombok.AllArgsConstructor;
import fr.monclub.club_service.club.mapper.UpdateClubInputToClubMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AddClubService {

    private final ClubCatalog clubCatalog;
    private final UpdateClubInputToClubMapper mapper;
    private final UpdateClubValidator validator;

    public Club apply(UpdateClubInput input, UUID ownerId) {
        Club existing = new Club();
        existing.setOwnerId(ownerId);
        validator.apply(input);
        return clubCatalog.save(mapper.updateClub(input, existing));
    }

}
