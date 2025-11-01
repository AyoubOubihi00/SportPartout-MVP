package fr.monclub.planning_service.unit.service;

import fr.monclub.planning_service.core.exception.InvalidSeanceStatusException;
import fr.monclub.planning_service.core.exception.PlanningAccessDeniedException;
import fr.monclub.planning_service.core.exception.SeanceNotFoundException;
import fr.monclub.planning_service.planning.catalog.SeanceCatalog;
import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import fr.monclub.planning_service.planning.repository.SeanceRepository;
import fr.monclub.planning_service.planning.service.SeanceDecisionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeanceDecisionServiceTest {

    @Mock
    private SeanceCatalog catalog;

    @Mock
    private SeanceRepository repository;

    @InjectMocks
    private SeanceDecisionService service;

    @Test
    void acceptShouldPromotePendingSeanceToOpen() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        Seance seance = Seance.builder()
                .id(seanceId)
                .idClub(clubId)
                .status(SeanceStatus.PENDING)
                .refusalReason("ancienne raison")
                .build();

        when(repository.findById(seanceId)).thenReturn(Optional.of(seance));
        when(catalog.save(seance)).thenReturn(seance);

        Seance result = service.accept(seanceId, clubId);

        assertSame(seance, result);
        assertEquals(SeanceStatus.OPEN, seance.getStatus());
        assertNull(seance.getRefusalReason());
        verify(catalog).save(seance);
    }

    @Test
    void refuseShouldCancelPendingSeance() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        Seance seance = Seance.builder()
                .id(seanceId)
                .idClub(clubId)
                .status(SeanceStatus.PENDING)
                .build();

        when(repository.findById(seanceId)).thenReturn(Optional.of(seance));
        when(catalog.save(seance)).thenReturn(seance);

        Seance result = service.refuse(seanceId, clubId, "   motif manquant d'entraineur   ");

        assertSame(seance, result);
        assertEquals(SeanceStatus.CANCELED, seance.getStatus());
        assertEquals("motif manquant d'entraineur", seance.getRefusalReason());
        verify(catalog).save(seance);
    }

    @Test
    void shouldFailWhenSeanceNotOwnedByClub() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        Seance seance = Seance.builder()
                .id(seanceId)
                .idClub(UUID.randomUUID())
                .status(SeanceStatus.PENDING)
                .build();

        when(repository.findById(seanceId)).thenReturn(Optional.of(seance));

        assertThrows(PlanningAccessDeniedException.class, () -> service.accept(seanceId, clubId));
        verify(catalog, never()).save(any());
    }

    @Test
    void shouldFailWhenSeanceNotPending() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        Seance seance = Seance.builder()
                .id(seanceId)
                .idClub(clubId)
                .status(SeanceStatus.OPEN)
                .build();

        when(repository.findById(seanceId)).thenReturn(Optional.of(seance));

        assertThrows(InvalidSeanceStatusException.class, () -> service.accept(seanceId, clubId));
        verify(catalog, never()).save(any());
    }

    @Test
    void shouldPropagateWhenSeanceNotFound() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        when(repository.findById(seanceId)).thenReturn(Optional.empty());

        assertThrows(SeanceNotFoundException.class, () -> service.accept(seanceId, clubId));
    }

    @Test
    void refuseShouldFailWhenReasonMissing() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        Seance seance = Seance.builder()
                .id(seanceId)
                .idClub(clubId)
                .status(SeanceStatus.PENDING)
                .build();

        when(repository.findById(seanceId)).thenReturn(Optional.of(seance));

        assertThrows(IllegalArgumentException.class, () -> service.refuse(seanceId, clubId, " "));
        verify(catalog, never()).save(any());
    }
}
