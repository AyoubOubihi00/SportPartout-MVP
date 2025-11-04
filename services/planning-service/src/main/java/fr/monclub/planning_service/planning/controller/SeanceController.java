package fr.monclub.planning_service.planning.controller;

import fr.monclub.planning_service.planning.catalog.SeanceCatalog;
import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceWithAvailability;
import fr.monclub.planning_service.planning.service.GetUser;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SeanceController {

    private final SeanceCatalog catalog;
    private final GetUser getUser;

    // Récupérer une séance par id
    @QueryMapping
    public Seance seance(@Argument UUID id) {
        return catalog.getById(id);
    }

    // Séances d’un club sur une période temporelle
    @QueryMapping
    public List<Seance> seancesByClubBetween(
            @Argument UUID clubId,
            @Argument OffsetDateTime start,
            @Argument OffsetDateTime end
    ) {
        return catalog.findByClubBetween(clubId, start, end);
    }

    // Séances d’un coach sur une période temporelle
    @QueryMapping
    public List<Seance> seancesByCoachBetween(
            @Argument UUID coachId,
            @Argument OffsetDateTime start,
            @Argument OffsetDateTime end
    ) {
        return catalog.findByCoachBetween(coachId, start, end);
    }

    // Version pratique avec disponibilité calculée
    @QueryMapping
    public List<SeanceWithAvailability> seancesWithAvailabilityByClubBetween(
            @Argument UUID clubId,
            @Argument OffsetDateTime start,
            @Argument OffsetDateTime end
    ) {
        return catalog.findWithAvailabilityByClubBetween(clubId, start, end);
    }

    // Inscription à une séance
    @MutationMapping
    public Seance joinSeance(@Argument UUID id) {
        return catalog.join(id);
    }

    // Désinscription d'une séance
    @MutationMapping
    public Seance leaveSeance(@Argument UUID id) {
        return catalog.leave(id);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<SeanceWithAvailability> mySeances(){
        System.out.println(getUser.apply().getId());
        return catalog.findByUserId(getUser.apply().getId())
                .stream()
                .map(SeanceWithAvailability::fromSeance)
                .toList();
    }
}
