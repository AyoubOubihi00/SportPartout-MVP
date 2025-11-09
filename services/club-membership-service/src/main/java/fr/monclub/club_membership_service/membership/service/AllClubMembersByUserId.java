package fr.monclub.club_membership_service.membership.service;


import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AllClubMembersByUserId {

    private final ClubMemberCatalog clubMemberCatalog;

    public AllClubMembersByUserId(ClubMemberCatalog clubMemberCatalog) {
        this.clubMemberCatalog = clubMemberCatalog;
    }

    public List<ClubMember> findClubIdsByUserId(UUID userId) {

        return clubMemberCatalog.findClubMembersByUserId(userId);
    }

}
