package fr.monclub.club_membership_service.membership.service;


import fr.monclub.club_membership_service.membership.Mapper.ClubMemberMapper;
import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UnSunbscribeMemberService {

    private final ClubMemberCatalog clubMemberCatalog;

    public UnSunbscribeMemberService(ClubMemberCatalog clubMemberCatalog, ClubMemberMapper mapper) {
        this.clubMemberCatalog = clubMemberCatalog;
    }

    public int unSubscribe(UUID clubId, UUID userId) {
        ClubMember clubMember = clubMemberCatalog.findByClubIdAndUserId(clubId, userId);
        if (clubMember == null) return 0;

        clubMember.setIsActif(false);
        clubMember.setDateSubscription(null);
        clubMember.setHimself(true);
        clubMember.setDateUnsubscription(OffsetDateTime.now());

        try {
            return clubMemberCatalog.deactivateClubMemberById(clubMember.getId());
        } catch (Exception e) {
            // Logger l'erreur correctement
            System.err.println("Erreur lors de la désactivation du membre : " + e.getMessage());
            return 0;
        }
    }
}
