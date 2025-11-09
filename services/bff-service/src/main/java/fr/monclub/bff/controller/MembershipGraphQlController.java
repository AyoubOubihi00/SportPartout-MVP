package fr.monclub.bff.controller;

import fr.monclub.bff.dto.UserInput;
import fr.monclub.bff.dto.SubscribeResult;
import fr.monclub.bff.service.MembershipBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MembershipGraphQlController {

    private final MembershipBffService membershipService;

    @QueryMapping
    public String salut() {
        System.out.println("salut has been called ");
        return "salut from membership BFF";
    }

    @QueryMapping
    public List<SubscribeResult> myClubs(@Argument UUID userId) {
        System.out.println("myClubs has been called for userId=" + userId);
        return membershipService.myClubs(userId);
    }

    @MutationMapping
    public SubscribeResult subscribeToClub(@Argument UUID clubId,
                                           @Argument("newMember") UserInput newMember) {
        System.out.println("subscribetoclub has been called ");
        return membershipService.subscribeToClub(clubId, newMember);
    }
}
