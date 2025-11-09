package fr.monclub.bff.controller;

import fr.monclub.bff.exception.BadRequestException;
import fr.monclub.bff.service.ClubListingBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ClubGraphQlController {

    private final ClubListingBffService clubListingService;

    @QueryMapping
    public List<ClubListingBffService.ClubSummary> clubs(@Argument UUID userId) {
        if (userId == null) {
            throw new BadRequestException("Le parametre userId est obligatoire");
        }
        return clubListingService.clubsForUser(userId);
    }

    @QueryMapping
    public List<ClubListingBffService.ClubSummary> managedClubs(@Argument UUID ownerId) {
        if (ownerId == null) {
            throw new BadRequestException("Le parametre ownerId est obligatoire");
        }
        return clubListingService.clubsManagedBy(ownerId);
    }


}
