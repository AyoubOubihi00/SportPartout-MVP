package fr.monclub.planning_service.planning.controller;

import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.service.SeanceDecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SeanceMutationController {

    private final SeanceDecisionService decisionService;

    @MutationMapping
    public Seance acceptSeance(@Argument UUID seanceId, @Argument UUID clubId) {
        return decisionService.accept(seanceId, clubId);
    }

    @MutationMapping
    public Seance refuseSeance(@Argument UUID seanceId, @Argument UUID clubId, @Argument String reason) {
        return decisionService.refuse(seanceId, clubId, reason);
    }
}
