package fr.monclub.planning_service.planning.repository;

import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface SeanceRepositoryCustom {

    // Recherche multi-critères 
    List<Seance> search(
            UUID idClub,
            UUID idCoach,
            UUID idTerrain,
            OffsetDateTime from,
            OffsetDateTime to,
            SeanceStatus status
    );

    // Chevauchement “métier”
    boolean hasOverlap(UUID idClub, UUID idTerrain, OffsetDateTime start, OffsetDateTime end, UUID excludeId);
}
