package fr.monclub.planning_service.planning.model;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SeanceWithAvailability {

    private final UUID id;
    private final UUID idClub;
    private final UUID idTerrain;
    private final UUID idCoach;

    private final OffsetDateTime startTime;
    private final OffsetDateTime endTime;
    private final long durationMinutes;   // champ calculé

    private final Integer capacite;
    private final Integer capaSeance;
    private final Integer remainingPlaces; // champ calculé
    private final boolean full;            // champ calculé

    private final UUID idSport;
    private final String nameSport;
    private final String description;
    private final SeanceStatus status;
    private final UUID createdBy;

    public SeanceWithAvailability(Seance s) {
        this(
            s.getId(),
            s.getIdClub(),
            s.getIdTerrain(),
            s.getIdCoach(),
            s.getStartTime(),
            s.getEndTime(),
            s.getStartTime() != null && s.getEndTime() != null
                ? Duration.between(s.getStartTime(), s.getEndTime()).toMinutes()
                : 0,
            s.getCapacite(),
            s.getCapaSeance(),
            (s.getCapacite() != null && s.getCapaSeance() != null)
                ? Math.max(0, s.getCapacite() - s.getCapaSeance())
                : 0,
            (s.getCapacite() != null && s.getCapaSeance() != null)
                && s.getCapaSeance() >= s.getCapacite(),
            s.getIdSport(),
            s.getNameSport(),
            s.getDescription(),
            s.getStatus(),
            s.getCreatedBy()
        );
    }
    public static SeanceWithAvailability fromSeance(Seance s) {
        return new SeanceWithAvailability(s);
    }

    public int remaining() { return remainingPlaces == null ? 0 : remainingPlaces; }
}
