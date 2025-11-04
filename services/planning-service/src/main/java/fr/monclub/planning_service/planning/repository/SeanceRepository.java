package fr.monclub.planning_service.planning.repository;

import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SeanceRepository extends JpaRepository<Seance, UUID>, SeanceRepositoryCustom {

    //  Lister les séances d’un club dans une fenêtre temporelle
    List<Seance> findByIdClubAndStartTimeBetweenOrderByStartTimeAsc(
            UUID idClub, OffsetDateTime startInclusive, OffsetDateTime endExclusive
    );

    List<Seance> findByIdCoachAndStartTimeBetweenOrderByStartTimeAsc(
            UUID idCoach, OffsetDateTime startInclusive, OffsetDateTime endExclusive
    );

    //  Filtrer par statut
    List<Seance> findByIdClubAndStatusOrderByStartTimeAsc(UUID idClub, SeanceStatus status);

    //  Lecture simple
    Optional<Seance> findByIdAndIdClub(UUID id, UUID idClub);

    // ---- Overlap TERRAIN (même club) ----
    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
        FROM Seance s
        WHERE s.idClub = :idClub
          AND s.idTerrain = :idTerrain
          AND (:excludeId IS NULL OR s.id <> :excludeId)
          AND s.status <> fr.monclub.planning_service.planning.model.SeanceStatus.CANCELED
          AND s.startTime < :endTime
          AND s.endTime   > :startTime
        """)
    boolean existsOverlapOnTerrain(
            @Param("idClub") UUID idClub,
            @Param("idTerrain") UUID idTerrain,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime,
            @Param("excludeId") UUID excludeId
    );

    // ---- Overlap COACH (même club) ----
    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
        FROM Seance s
        WHERE s.idClub  = :idClub
          AND s.idCoach = :idCoach
          AND (:excludeId IS NULL OR s.id <> :excludeId)
          AND s.status <> fr.monclub.planning_service.planning.model.SeanceStatus.CANCELED
          AND s.startTime < :endTime
          AND s.endTime   > :startTime
        """)
    boolean existsOverlapOnCoach(
            @Param("idClub") UUID idClub,
            @Param("idCoach") UUID idCoach,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime,
            @Param("excludeId") UUID excludeId
    );

    // Incrémenter / décrémenter la capacité utilisée (inscriptions) de manière atomique
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Seance s
           SET s.capaSeance = s.capaSeance + 1
         WHERE s.id = :id
           AND s.capaSeance < s.capacite
           AND s.status = fr.monclub.planning_service.planning.model.SeanceStatus.OPEN
        """)
    int tryIncrementCapa(@Param("id") UUID id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Seance s
           SET s.capaSeance = s.capaSeance - 1
         WHERE s.id = :id
           AND s.capaSeance > 0
        """)
    int tryDecrementCapa(@Param("id") UUID id);

    List<Seance> findAllByIdSport(UUID idSport);

    List<Seance> findAllByParticipantsContains(UUID userId);
}
