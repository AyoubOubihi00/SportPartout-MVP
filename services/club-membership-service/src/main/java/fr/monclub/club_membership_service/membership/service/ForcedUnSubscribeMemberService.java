package fr.monclub.club_membership_service.membership.service;


import fr.monclub.club_membership_service.membership.Mapper.ClubMemberMapper;
import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ForcedUnSubscribeMemberService {

    private final ClubMemberCatalog clubMemberCatalog;


    public ForcedUnSubscribeMemberService(ClubMemberCatalog clubMemberCatalog, ClubMemberMapper mapper) {
        this.clubMemberCatalog = clubMemberCatalog;
    }


    public int forcedUnsubscription(UUID clubId, UUID userId) {
        ClubMember clubMember = clubMemberCatalog.findByClubIdAndUserId(clubId, userId);
        if (clubMember == null) return 0;
        clubMember.setIsActif(false);
        clubMember.setDateSubscription(null);
        clubMember.setHimself(false);

        try {
            return clubMemberCatalog.deactivateClubMemberById(clubMember.getId());
            // L'opération a réussi, on retourne true.
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du membre : " + e.getMessage());
            return 0;
        }
    }
}
