package fr.monclub.club_membership_service.service;

import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.model.ClubMember;
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
