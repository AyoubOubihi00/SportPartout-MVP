package fr.monclub.planning_service.unit.catalog;

import fr.monclub.planning_service.core.exception.InvalidDatesException;
import fr.monclub.planning_service.core.exception.OverlapException;
import fr.monclub.planning_service.core.exception.SeanceNotFoundException;
import fr.monclub.planning_service.planning.catalog.SeanceCatalogImpl;
import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import fr.monclub.planning_service.planning.model.SeanceWithAvailability;
import fr.monclub.planning_service.planning.repository.SeanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SeanceCatalogImplTest {

    @Mock
    private SeanceRepository repo;

    @InjectMocks
    private SeanceCatalogImpl catalog;

    
    private UUID clubId;
    private UUID terrainId;
    private UUID coachId;
    private UUID sportId;

    private OffsetDateTime t10, t11, t1030, t1045;

    @BeforeEach
    void setUp() {
        clubId   = UUID.fromString("5a14f7fd-db95-4bd0-a2b9-5f8f1c59d557");
        terrainId= UUID.fromString("ffd046bf-106e-4f40-a47e-b509ce9d9811");
        coachId  = UUID.fromString("1a4bb0ab-68f6-46a1-a638-d175441a6030");
        sportId  = UUID.fromString("6bdd138d-a7fd-45a2-8706-4f056921020a");

        t10   = OffsetDateTime.parse("2025-01-01T10:00:00Z");
        t11   = OffsetDateTime.parse("2025-01-01T11:00:00Z");
        t1030 = OffsetDateTime.parse("2025-01-01T10:30:00Z");
        t1045 = OffsetDateTime.parse("2025-01-01T10:45:00Z");
    }

    // ----------- LECTURE -----------

    @Test
    void getById_retourneSeanceOuNotFound() {
        UUID id = UUID.randomUUID();
        Seance s = baseSeanceBuilder().id(id).build();

        when(repo.findById(id)).thenReturn(Optional.of(s));
        assertThat(catalog.getById(id)).isSameAs(s);

        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> catalog.getById(id))
                .isInstanceOf(SeanceNotFoundException.class);
    }

    @Test
    void findByClubBetween_validePeriodeEtDelegueAuRepo() {
        when(repo.findByIdClubAndStartTimeBetweenOrderByStartTimeAsc(eq(clubId), eq(t10), eq(t11)))
                .thenReturn(List.of());

        var res = catalog.findByClubBetween(clubId, t10, t11);
        assertThat(res).isEmpty();

        // dates invalides -> exception
        assertThatThrownBy(() -> catalog.findByClubBetween(clubId, t11, t10))
                .isInstanceOf(InvalidDatesException.class);
    }

    @Test
    void seancesWithAvailabilityByClubBetween_mapBienLesDisponibilites() {
        // 1 séance avec capacite=5, capaSeance=3 => remaining=2
        Seance s = baseSeanceBuilder()
                .capacite(5).capaSeance(3)
                .build();
        when(repo.findByIdClubAndStartTimeBetweenOrderByStartTimeAsc(eq(clubId), eq(t10), eq(t11)))
                .thenReturn(List.of(s));

        List<SeanceWithAvailability> out =
                catalog.findWithAvailabilityByClubBetween(clubId, t10, t11);

        assertThat(out).hasSize(1);
        assertThat(out.get(0).remaining()).isEqualTo(2);
        assertThat(out.get(0).isFull()).isFalse();
    }

    // ----------- ECRITURE -----------

    @Test
    void save_refuseDatesInvalides() {
        Seance bad = baseSeanceBuilder()
                .startTime(t11).endTime(t10) // fin avant début
                .build();

        assertThatThrownBy(() -> catalog.save(bad))
                .isInstanceOf(InvalidDatesException.class);
    }

    @Test
    void save_refuseChevauchementTerrain() {
        Seance s = baseSeanceBuilder().build();

        // le repo signale un overlap terrain
        when(repo.existsOverlapOnTerrain(eq(clubId), eq(terrainId), eq(t10), eq(t11), isNull()))
                .thenReturn(true);

        assertThatThrownBy(() -> catalog.save(s))
                .isInstanceOf(OverlapException.class);
    }

    @Test
    void save_refuseChevauchementCoachQuandCoachNonNull() {
        Seance s = baseSeanceBuilder().build();

        when(repo.existsOverlapOnTerrain(any(), any(), any(), any(), any()))
                .thenReturn(false);
        when(repo.existsOverlapOnCoach(eq(clubId), eq(coachId), eq(t10), eq(t11), isNull()))
                .thenReturn(true);

        assertThatThrownBy(() -> catalog.save(s))
                .isInstanceOf(OverlapException.class);
    }

    @Test
    void save_appliqueBusinessRules_fullOuOpen() {
        // on capture l’argument passé à repo.save pour vérifier le statut qu'on vient de calculer
        ArgumentCaptor<Seance> captor = ArgumentCaptor.forClass(Seance.class);
        when(repo.existsOverlapOnTerrain(any(), any(), any(), any(), any())).thenReturn(false);
        when(repo.existsOverlapOnCoach(any(), any(), any(), any(), any())).thenReturn(false);
        when(repo.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        // Cas 1: capa=capacite => FULL
        Seance full = baseSeanceBuilder()
                .capacite(3).capaSeance(3)
                .status(null) 
                .build();
        catalog.save(full);
        assertThat(captor.getValue().getStatus()).isEqualTo(SeanceStatus.FULL);

        // Cas 2: capa<capacite => OPEN
        Seance open = baseSeanceBuilder()
                .capacite(5).capaSeance(2)
                .status(null)
                .build();
        catalog.save(open);
        assertThat(captor.getValue().getStatus()).isEqualTo(SeanceStatus.OPEN);

        // Cas 3: CANCELED reste CANCELED
        Seance canceled = baseSeanceBuilder()
                .capacite(5).capaSeance(5)
                .status(SeanceStatus.CANCELED)
                .build();
        catalog.save(canceled);
        assertThat(captor.getValue().getStatus()).isEqualTo(SeanceStatus.CANCELED);
    }

    // ----------- Test_Helpers -----------

    private Seance.SeanceBuilder baseSeanceBuilder() {
        return Seance.builder()
                .idClub(clubId)
                .idTerrain(terrainId)
                .idCoach(coachId)
                .startTime(t10)
                .endTime(t11)
                .capacite(10)
                .capaSeance(0)
                .idSport(sportId)
                .nameSport("Foot")
                .status(SeanceStatus.OPEN);
    }
}
