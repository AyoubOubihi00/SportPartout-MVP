package fr.monclub.planning_service.planning.catalog;

import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceWithAvailability;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface SeanceCatalog {

    // lecture
    Seance getById(UUID id);

    List<Seance> findByClubBetween(UUID clubId, OffsetDateTime start, OffsetDateTime end);

    List<Seance> findByCoachBetween(UUID coachId, OffsetDateTime start, OffsetDateTime end);

    // lecture
    List<SeanceWithAvailability> findWithAvailabilityByClubBetween(
            UUID clubId, OffsetDateTime start, OffsetDateTime end);

    // écriture 
    Seance save(Seance seance);

    Seance saveAndFlush(Seance seance);

    // business: inscription à une séance (capacité + statut)
    Seance join(UUID seanceId);

    // business: désinscription d'une séance (capacité + statut)
    Seance leave(UUID seanceId);

    List<Seance> findByUserId(UUID userId);
}
