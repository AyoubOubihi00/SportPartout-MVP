package fr.monclub.bff.service;

import fr.monclub.bff.exception.BadRequestException;
import fr.monclub.bff.exception.ForbiddenOperationException;
import fr.monclub.bff.gateway.ClubGateway;
import fr.monclub.bff.gateway.PlanningGateway;
import fr.monclub.bff.model.SeanceStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeanceDecisionBffServiceTest {

    @Mock
    private PlanningGateway planningGateway;

    @Mock
    private ClubGateway clubGateway;

    @InjectMocks
    private SeanceDecisionBffService service;

    @Test
    void acceptShouldDelegateToPlanningWhenOwnerMatches() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        PlanningGateway.SeanceDto seance = new PlanningGateway.SeanceDto(seanceId, clubId, SeanceStatus.PENDING, null);
        ClubGateway.ClubDto club = new ClubGateway.ClubDto(clubId, userId, "Club House");
        PlanningGateway.SeanceDto accepted = new PlanningGateway.SeanceDto(seanceId, clubId, SeanceStatus.OPEN, null);

        when(planningGateway.getSeance(seanceId)).thenReturn(seance);
        when(clubGateway.getClub(clubId)).thenReturn(club);
        when(planningGateway.accept(seanceId, clubId)).thenReturn(accepted);

        SeanceDecisionBffService.DecisionResponse response = service.accept(seanceId, userId);

        assertEquals(SeanceStatus.OPEN, response.status());
        assertEquals(clubId, response.clubId());
        verify(planningGateway).accept(seanceId, clubId);
    }

    @Test
    void refuseShouldTrimReasonAndDelegate() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        PlanningGateway.SeanceDto seance = new PlanningGateway.SeanceDto(seanceId, clubId, SeanceStatus.PENDING, null);
        ClubGateway.ClubDto club = new ClubGateway.ClubDto(clubId, userId, "Club House");
        PlanningGateway.SeanceDto refused = new PlanningGateway.SeanceDto(seanceId, clubId, SeanceStatus.CANCELED, "Coach indisponible");

        when(planningGateway.getSeance(seanceId)).thenReturn(seance);
        when(clubGateway.getClub(clubId)).thenReturn(club);
        when(planningGateway.refuse(seanceId, clubId, "Coach indisponible")).thenReturn(refused);

        SeanceDecisionBffService.DecisionResponse response = service.refuse(seanceId, userId, "  Coach indisponible  ");

        assertEquals(SeanceStatus.CANCELED, response.status());
        verify(planningGateway).refuse(seanceId, clubId, "Coach indisponible");
    }

    @Test
    void refuseShouldFailWhenReasonMissing() {
        UUID seanceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        assertThrows(BadRequestException.class, () -> service.refuse(seanceId, userId, " "));
    }

    @Test
    void acceptShouldFailWhenUserNotOwner() {
        UUID seanceId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        PlanningGateway.SeanceDto seance = new PlanningGateway.SeanceDto(seanceId, clubId, SeanceStatus.PENDING, null);
        ClubGateway.ClubDto club = new ClubGateway.ClubDto(clubId, ownerId, "Club House");

        when(planningGateway.getSeance(seanceId)).thenReturn(seance);
        when(clubGateway.getClub(clubId)).thenReturn(club);

        assertThrows(ForbiddenOperationException.class, () -> service.accept(seanceId, userId));
    }
}
