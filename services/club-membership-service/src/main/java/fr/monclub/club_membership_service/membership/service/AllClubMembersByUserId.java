package fr.monclub.club_membership_service.membership.service;


import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class AllClubMembersByUserId {

    private final ClubMemberCatalog clubMemberCatalog;

    public AllClubMembersByUserId(ClubMemberCatalog clubMemberCatalog) {
        this.clubMemberCatalog = clubMemberCatalog;
    }

    public List<ClubMember> findClubIdsByUserId(UUID userId) {
        List<ClubMember> memberships = clubMemberCatalog.findClubMembersByUserId(userId);
        if (memberships == null || memberships.isEmpty()) {
            return Collections.emptyList();
        }
        return memberships.stream()
                .filter(member -> Boolean.TRUE.equals(member.getIsActif()))
                .collect(Collectors.toList());
    }

}
