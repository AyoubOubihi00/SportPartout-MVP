package fr.monclub.planning_service.planning.catalog;

import fr.monclub.planning_service.core.exception.InvalidDatesException;
import fr.monclub.planning_service.core.exception.OverlapException;
import fr.monclub.planning_service.core.exception.SeanceNotFoundException;
import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import fr.monclub.planning_service.planning.model.SeanceWithAvailability;
import fr.monclub.planning_service.planning.repository.SeanceRepository;
import fr.monclub.planning_service.planning.service.GetUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeanceCatalogImpl implements SeanceCatalog {

    private final SeanceRepository repo;
    private final GetUser getUser;

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

    // ---------- ECRITURE ----------

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

    @Override
    @Transactional
    public Seance join(UUID seanceId) {
        // Vérifie l'existence d'abord
        Seance s = getById(seanceId);
        if (s.getStatus() == SeanceStatus.CANCELED) {
            throw new fr.monclub.planning_service.core.exception.CapacityFullException("Séance annulée : inscription impossible.");
        }

        UUID userId = getUser.apply().getId();
        Set<UUID> participants = s.getParticipants();
        if (participants.contains(userId)) {
            ensureBusinessRules(s);
            return repo.saveAndFlush(s);
        }
        if (s.getCapacite() != null && s.getCapaSeance() != null && s.getCapaSeance() >= s.getCapacite()) {
            throw new fr.monclub.planning_service.core.exception.CapacityFullException();
        }
        participants.add(userId);
        s.setCapaSeance(participants.size());
        ensureBusinessRules(s);
        return repo.saveAndFlush(s);
    }

    @Override
    @Transactional
    public Seance leave(UUID seanceId) {
        Seance s = getById(seanceId);

        UUID userId = getUser.apply().getId();
        if (userId == null) {
            int updated = repo.tryDecrementCapa(seanceId);
            if (updated == 0) {
                throw new fr.monclub.planning_service.core.exception.NoRegistrationException();
            }
            Seance reloaded = getById(seanceId);
            ensureBusinessRules(reloaded);
            return repo.saveAndFlush(reloaded);
        }

        Set<UUID> participants = s.getParticipants();
        if (!participants.remove(userId)) {
            throw new fr.monclub.planning_service.core.exception.NoRegistrationException();
        }
        s.setCapaSeance(participants.size());
        ensureBusinessRules(s);
        return repo.saveAndFlush(s);
    }

    @Override
    public List<Seance> findByUserId(UUID userId, OffsetDateTime start, OffsetDateTime end) {
        if (start == null || end == null) {
            return repo.findAllByParticipantsContains(userId);
        }
        return repo.findAllByParticipantsContainsAndStartTimeBetween(userId, start, end);
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
        UUID excludeId = s.getId();

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