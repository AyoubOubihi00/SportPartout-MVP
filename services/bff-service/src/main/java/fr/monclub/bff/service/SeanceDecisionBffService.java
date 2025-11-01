package fr.monclub.bff.service;

import fr.monclub.bff.exception.BadRequestException;
import fr.monclub.bff.exception.ForbiddenOperationException;
import fr.monclub.bff.gateway.ClubGateway;
import fr.monclub.bff.gateway.PlanningGateway;
import fr.monclub.bff.model.SeanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeanceDecisionBffService {

    private final PlanningGateway planningGateway;
    private final ClubGateway clubGateway;

    public DecisionResponse accept(UUID seanceId, UUID userId) {
        PlanningGateway.SeanceDto seance = planningGateway.getSeance(seanceId);
        ClubGateway.ClubDto club = clubGateway.getClub(seance.clubId());
        ensureOwnership(userId, club);

        PlanningGateway.SeanceDto updated = planningGateway.accept(seanceId, club.id());
        return DecisionResponse.from(updated);
    }

    public DecisionResponse refuse(UUID seanceId, UUID userId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BadRequestException("Le motif de refus est obligatoire");
        }

        PlanningGateway.SeanceDto seance = planningGateway.getSeance(seanceId);
        ClubGateway.ClubDto club = clubGateway.getClub(seance.clubId());
        ensureOwnership(userId, club);

        PlanningGateway.SeanceDto updated = planningGateway.refuse(seanceId, club.id(), reason.trim());
        return DecisionResponse.from(updated);
    }

    private void ensureOwnership(UUID userId, ClubGateway.ClubDto club) {
        if (!club.ownerId().equals(userId)) {
            throw new ForbiddenOperationException(
                    "Seul le proprietaire du club %s peut statuer sur cette seance".formatted(club.id()));
        }
    }

    public record DecisionResponse(UUID id, UUID clubId, SeanceStatus status, String refusalReason) {
        static DecisionResponse from(PlanningGateway.SeanceDto seance) {
            return new DecisionResponse(seance.id(), seance.clubId(), seance.status(), seance.refusalReason());
        }
    }
}
