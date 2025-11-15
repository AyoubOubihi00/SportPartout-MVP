package fr.monclub.bff.controller;

import fr.monclub.bff.exception.BadRequestException;
import fr.monclub.bff.service.ClubListingBffService;
import fr.monclub.bff.gateway.ClubGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import fr.monclub.bff.dto.UpdateClubInput;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ClubGraphQlController {

    private final ClubListingBffService clubListingService;
    private final ClubGateway clubGateway;

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

    @QueryMapping
    public ClubGateway.ClubDto clubById(@Argument UUID id) {
        if (id == null) {
            throw new BadRequestException("Le parametre id est obligatoire");
        }
        return clubGateway.getClub(id);
    }

    @MutationMapping
    public ClubGateway.ClubDto updateClub(@Argument UUID id,
                                         @Argument(name = "data") UpdateClubInput data,
                                         @Argument UUID ownerId) {
        if (id == null) {
            throw new BadRequestException("Le parametre id est obligatoire");
        }
        Map<String, Object> vars = data != null ? data.toGraphqlMap() : Map.of();
        return clubGateway.updateClub(id, vars, ownerId);
    }

    @MutationMapping
    public Boolean disableClub(@Argument UUID id, @Argument UUID ownerId) {
        if (id == null || ownerId == null) {
            throw new BadRequestException("Les parametres id et ownerId sont obligatoires");
        }
        return clubGateway.disableClub(id, ownerId);
    }

    @MutationMapping
    public Boolean reactivateClub(@Argument UUID id, @Argument UUID ownerId) {
        if (id == null || ownerId == null) {
            throw new BadRequestException("Les parametres id et ownerId sont obligatoires");
        }
        return clubGateway.reactivateClub(id, ownerId);
    }


}
