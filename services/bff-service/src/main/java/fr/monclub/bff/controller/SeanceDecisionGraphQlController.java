package fr.monclub.bff.controller;

import fr.monclub.bff.service.SeanceDecisionBffService;
import fr.monclub.bff.service.SeanceDecisionBffService.DecisionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SeanceDecisionGraphQlController {

    private final SeanceDecisionBffService decisionService;

    @MutationMapping
    public DecisionResponse acceptPendingSeance(@Argument UUID seanceId,
                                                @Argument UUID userId) {
        return decisionService.accept(seanceId, userId);
    }

    @MutationMapping
    public DecisionResponse refusePendingSeance(@Argument UUID seanceId,
                                                @Argument UUID userId,
                                                @Argument String reason) {
        return decisionService.refuse(seanceId, userId, reason);
    }
}
