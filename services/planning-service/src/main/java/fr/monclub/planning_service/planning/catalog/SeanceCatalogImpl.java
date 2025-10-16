package fr.monclub.planning_service.planning.catalog;

import fr.monclub.planning_service.core.exception.InvalidDatesException;
import fr.monclub.planning_service.core.exception.OverlapException;
import fr.monclub.planning_service.core.exception.SeanceNotFoundException;
import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import fr.monclub.planning_service.planning.model.SeanceWithAvailability;
import fr.monclub.planning_service.planning.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeanceCatalogImpl implements SeanceCatalog {

    private final SeanceRepository repo;

    // ---------- LECTURE ----------

    @Override
    @Transactional(readOnly = true)
    public Seance getById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new SeanceNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seance> findByClubBetween(UUID clubId, OffsetDateTime start, OffsetDateTime end) {
        var period = normalizedPeriod(start, end);
        return repo.findByIdClubAndStartTimeBetweenOrderByStartTimeAsc(
                clubId, period.start(), period.end()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seance> findByCoachBetween(UUID coachId, OffsetDateTime start, OffsetDateTime end) {
        var period = normalizedPeriod(start, end);
        return repo.findByIdCoachAndStartTimeBetweenOrderByStartTimeAsc(
                coachId, period.start(), period.end()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeanceWithAvailability> findWithAvailabilityByClubBetween(
            UUID clubId, OffsetDateTime start, OffsetDateTime end) {
        return findByClubBetween(clubId, start, end)
                .stream()
                .map(SeanceWithAvailability::fromSeance) 
                .toList();
    }

    // ---------- ÉCRITURE ----------

    @Override
    @Transactional
    public Seance save(Seance seance) {
        validateDates(seance);
        ensureBusinessRules(seance);
        checkOverlaps(seance);
        return repo.save(seance);
    }

    @Override
    @Transactional
    public Seance saveAndFlush(Seance seance) {
        validateDates(seance);
        ensureBusinessRules(seance);
        checkOverlaps(seance);
        return repo.saveAndFlush(seance);
    }

    // ---------- Helpers métier ----------

    private record Period(OffsetDateTime start, OffsetDateTime end) {}

    private Period normalizedPeriod(OffsetDateTime start, OffsetDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new InvalidDatesException();
        }
        return new Period(start, end);
    }

    private void validateDates(Seance s) {
        if (s.getStartTime() == null || s.getEndTime() == null || !s.getEndTime().isAfter(s.getStartTime())) {
            throw new InvalidDatesException();
        }
    }

    /**
     * Règles simples :
     * - capaSeance ne dépasse pas capacite
     * - status = FULL si capaSeance == capacite et status != CANCELED
     * - status = OPEN sinon (si pas PENDING/CANCELED)
     */
    private void ensureBusinessRules(Seance s) {
        if (s.getCapacite() == null || s.getCapacite() < 0) s.setCapacite(0);
        if (s.getCapaSeance() == null || s.getCapaSeance() < 0) s.setCapaSeance(0);
        if (s.getCapaSeance() > s.getCapacite()) s.setCapaSeance(s.getCapacite());

        if (s.getStatus() == null) s.setStatus(SeanceStatus.OPEN);
        if (s.getStatus() != SeanceStatus.CANCELED && s.getStatus() != SeanceStatus.PENDING) {
            s.setStatus(s.getCapaSeance().equals(s.getCapacite())
                    ? SeanceStatus.FULL
                    : SeanceStatus.OPEN);
        }
    }

    /**
     * On vérifie les chevauchements de créneau sur le même terrain et le même coach
     */
    private void checkOverlaps(Seance s) {
        UUID excludeId = s.getId(); // null en création, non null en mise à jour

        boolean terrainBusy = repo.existsOverlapOnTerrain(
                s.getIdClub(), s.getIdTerrain(), s.getStartTime(), s.getEndTime(), excludeId
        );
        if (terrainBusy) {
            throw new OverlapException(s.getIdTerrain(), s.getStartTime(), s.getEndTime());
        }

        if (s.getIdCoach() != null) {
            boolean coachBusy = repo.existsOverlapOnCoach(
                    s.getIdClub(), s.getIdCoach(), s.getStartTime(), s.getEndTime(), excludeId
            );
            if (coachBusy) {
                throw new OverlapException(s.getIdCoach(), s.getStartTime(), s.getEndTime());
            }
        }
    }
}
