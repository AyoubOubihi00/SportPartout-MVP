package fr.monclub.planning_service.planning.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "seance")
@EntityListeners(AuditingEntityListener.class) 
public class Seance {

    @Id
    @GeneratedValue
    private UUID id;                 // idSeance

    @Column(nullable = false)
    private UUID idClub;

    @Column(nullable = false)
    private UUID idTerrain;

    private UUID idCoach;

    @Column(nullable = false)
    private OffsetDateTime startTime;  // start

    @Column(nullable = false)
    private OffsetDateTime endTime;    // end

    @Column(nullable = false)
    private Integer capacite;          // capacité max

    @Column(nullable = false)
    private Integer capaSeance;        // nombre inscrits (0..capacite)

    @Column(nullable = false)
    private UUID idSport;

    @Column(nullable = false)
    private String nameSport;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeanceStatus status;       // OPEN | FULL | CANCELED | PENDING

    // --- Auditing Spring (automatique) ---
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    
    @CreatedBy
    @Column(updatable = false, nullable = true)
    private UUID createdBy;

    // Soft delete éventuel (géré manuellement)
    private OffsetDateTime deletedAt;

    private String refusalReason;

    // Liste des participants (ids utilisateurs)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "seance_participants",
            joinColumns = @JoinColumn(name = "seance_id")
    )
    @Column(name = "user_id", nullable = true)
    private Set<UUID> participants = new HashSet<>();

    private String coachName;

    private String clubName;
    private String adressSeance;
    private String citySeance;

    private String terrainName;
}
