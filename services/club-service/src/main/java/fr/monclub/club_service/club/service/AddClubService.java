package fr.monclub.club_service.club.service;

import fr.monclub.club_service.club.catalog.ClubCatalog;
import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.validators.UpdateClubValidator;
import fr.monclub.club_service.config.RabbitMQ.Publisher.ClubEventPublisher;
import lombok.AllArgsConstructor;
import fr.monclub.club_service.club.mapper.UpdateClubInputToClubMapper;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AddClubService {

    private final ClubCatalog clubCatalog;
    private final UpdateClubInputToClubMapper mapper;
    private final UpdateClubValidator validator;
    private final ClubEventPublisher clubEventPublisher;

    public Club apply(UpdateClubInput input, UUID ownerId) {
        System.out.println("Creating club for ownerId: " + ownerId);
        Club existing = new Club();
        existing.setOwnerId(ownerId);
        validator.apply(input);
        OffsetDateTime now = OffsetDateTime.now();
        Club club = mapper.updateClub(input, existing);
        club.setCreatedAt(now);
        club.setUpdatedAt(now);
        Club savedClub = clubCatalog.save(club);
        clubEventPublisher.publishClubCreated(savedClub);
        return savedClub;
    }

}
