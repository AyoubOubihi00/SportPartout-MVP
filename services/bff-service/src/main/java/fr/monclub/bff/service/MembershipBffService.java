package fr.monclub.bff.service;

import fr.monclub.bff.dto.UserInput;
import fr.monclub.bff.dto.SubscribeResult;
import fr.monclub.bff.exception.ResourceNotFoundException;
import fr.monclub.bff.gateway.ClubGateway;
import fr.monclub.bff.gateway.MembershipGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembershipBffService {

    private final MembershipGateway membershipGateway;
    private final ClubGateway clubGateway;

    public SubscribeResult subscribeToClub(UUID clubId, UserInput newMember) {
        System.out.println("MembershipBffService: subscribeToClub called with clubId=" + clubId + " and newMember=" + newMember);
        // Fetch club details first, so we can persist a snapshot in membership service
        ClubGateway.ClubDto club = clubGateway.getClub(clubId);
        MembershipGateway.MemberDto m = membershipGateway.subscribeToClub(clubId, newMember, club);
        if (m == null) return null;
        System.out.println(club.address());
        System.out.println("ON ESY BIEN LA DANS LE SERVICE MEMBERSHIPBFF");

        return new SubscribeResult(
                m.clubId(),
                m.userId(),
                m.firstName(),
                m.lastName(),
                m.birthDate(),
                m.email(),
                m.phoneNumber(),
                m.isActif(),
                m.dateSubscription(),
                null,
                m.himself(),
                club
        );
    }

    public int forcedUnsubscribe(UUID clubId, UUID memberId) {
        System.out.println("MembershipBffService: forcedUnsubscribe clubId=" + clubId + ", memberId=" + memberId);
        return membershipGateway.forcedUnsubscribe(clubId, memberId);
    }

    public List<SubscribeResult> myClubs(UUID userId) {
        List<MembershipGateway.MemberWithClubDto> memberships = membershipGateway.clubMembersByUserId(userId);
        List<SubscribeResult> results = new ArrayList<>(memberships.size());
        for (MembershipGateway.MemberWithClubDto membership : memberships) {
            ClubGateway.ClubDto club = membership.club();
            if (club == null && membership.clubId() != null) {
                try {
                    club = clubGateway.getClub(membership.clubId());
                } catch (ResourceNotFoundException ignored) {
                    // If the referenced club no longer exists, keep club information null.
                }
            }
            results.add(new SubscribeResult(
                    membership.clubId(),
                    membership.userId(),
                    membership.firstName(),
                    membership.lastName(),
                    membership.birthDate(),
                    membership.email(),
                    membership.phoneNumber(),
                    membership.isActif(),
                    membership.dateSubscription(),
                    membership.dateUnsubscription(),
                    membership.himself(),
                    club
            ));
        }
        return results;
    }

    public List<SubscribeResult> clubMembersByClubId(UUID clubId) {
        List<MembershipGateway.MemberWithClubDto> memberships = membershipGateway.clubMembersByClubId(clubId);
        List<SubscribeResult> results = new ArrayList<>(memberships.size());
        for (MembershipGateway.MemberWithClubDto membership : memberships) {
            ClubGateway.ClubDto club = membership.club();
            if (club == null && membership.clubId() != null) {
                try {
                    club = clubGateway.getClub(membership.clubId());
                } catch (ResourceNotFoundException ignored) {
                }
            }
            results.add(new SubscribeResult(
                    membership.clubId(),
                    membership.userId(),
                    membership.firstName(),
                    membership.lastName(),
                    membership.birthDate(),
                    membership.email(),
                    membership.phoneNumber(),
                    membership.isActif(),
                    membership.dateSubscription(),
                    membership.dateUnsubscription(),
                    membership.himself(),
                    club
            ));
        }
        return results;
    }

    public boolean unsubscribeFromClub(UUID clubId, UUID memberId) {
        return membershipGateway.unsubscribeFromClub(clubId, memberId);
    }
}
