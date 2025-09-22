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

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UpdateSportTypeService {

    private final ClubCatalog clubCatalog;

    public void apply(UUID id, String name) {
        List<Club> clubs = clubCatalog.getAllClubsBySportTypeId(id);
        clubs.forEach(club -> {
            club.setTypeName(name);
        });
        if (!clubs.isEmpty()) {
            clubCatalog.saveAll(clubs);
        }
    }

}
