package fr.monclub.club_membership_service.controller;

import fr.monclub.club_membership_service.DTO.UserInput;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.model.Role;
import fr.monclub.club_membership_service.service.*;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ClubMemberController {

    private final SubscribeMemberService subscribeMemberService;
    private final UnSunbscribeMemberService unSunbscribeMemberService;
    private final ForcedUnSubscribeMemberService forcedUnSubscribeMemberService;
    private final AllMembersService allMembersService;
    private final AllClubMembersByUserId clubByUserIdService;

    @MutationMapping
    @PreAuthorize("hasAnyRole('USER','OWNER')")
    public ClubMember subscribeToClub( @Argument UUID clubId, @Argument("newMember") UserInput newMember) {
        return subscribeMemberService.subscribe(clubId, newMember);

    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public int unsubscribeFromClub(@Argument UUID clubId,@Argument("memberId") UUID memberId) {
      return unSunbscribeMemberService.unSubscribe(clubId,memberId);
    }

    @MutationMapping
    @PreAuthorize("hasRole('OWNER')")
    public int forcedUnsubscription(@Argument UUID clubId,@Argument("memberId") UUID memberId) {
        return forcedUnSubscribeMemberService.forcedUnsubscription(clubId,memberId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('USER','OWNER')")
    public List<ClubMember> clubMembersByUserId(@Argument UUID userId) {
        return clubByUserIdService.findClubIdsByUserId(userId);
    }

    @QueryMapping
    @PreAuthorize("hasRole('OWNER')")
    public List<ClubMember> ActiveClubMembersByClubId(@Argument UUID clubId) {
        return allMembersService.getAllMembersIsActif(clubId);
    }

    @QueryMapping
    @PreAuthorize("hasRole('OWNER')")
    public List<ClubMember> IsNotActiveClubMembersByClubId(@Argument UUID clubId) {
        return allMembersService.getAllMembersNotActif(clubId);
    }


    @QueryMapping
    @PreAuthorize("hasRole('OWNER')")
    public List<ClubMember> AllClubMembersByClubId(@Argument UUID clubId) {
        return allMembersService.getMembersByClub(clubId);
    }

}
