package fr.monclub.planning_service.planning.service;

import fr.monclub.planning_service.core.exception.InvalidSeanceStatusException;
import fr.monclub.planning_service.core.exception.PlanningAccessDeniedException;
import fr.monclub.planning_service.core.exception.SeanceNotFoundException;
import fr.monclub.planning_service.planning.catalog.SeanceCatalog;
import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import fr.monclub.planning_service.planning.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeanceDecisionService {

    private final SeanceCatalog catalog;
    private final SeanceRepository repository;

    @Transactional
    public Seance accept(UUID seanceId, UUID clubId) {
        Seance seance = repository.findById(seanceId)
                .orElseThrow(() -> new SeanceNotFoundException(seanceId));
        ensureOwnedByClub(seance, clubId);
        ensurePending(seance);

        seance.setStatus(SeanceStatus.OPEN);
        seance.setRefusalReason(null);
        return catalog.save(seance);
    }

    @Transactional
    public Seance refuse(UUID seanceId, UUID clubId, String reason) {
        Seance seance = repository.findById(seanceId)
                .orElseThrow(() -> new SeanceNotFoundException(seanceId));
        ensureOwnedByClub(seance, clubId);
        ensurePending(seance);
        ensureReasonProvided(reason);

        seance.setStatus(SeanceStatus.CANCELED);
        seance.setRefusalReason(reason.trim());
        return catalog.save(seance);
    }

    private void ensureOwnedByClub(Seance seance, UUID clubId) {
        if (clubId == null || !seance.getIdClub().equals(clubId)) {
            throw new PlanningAccessDeniedException("Le gerant ne peut pas statuer sur une seance d'un autre club");
        }
    }

    private void ensurePending(Seance seance) {
        if (seance.getStatus() != SeanceStatus.PENDING) {
            throw new InvalidSeanceStatusException(seance.getStatus(), SeanceStatus.PENDING);
        }
    }

    private void ensureReasonProvided(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif de refus doit etre renseigne");
        }
    }
}
