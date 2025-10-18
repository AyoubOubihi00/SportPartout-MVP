package fr.monclub.club_membership_service.service;

import fr.monclub.club_membership_service.Mapper.ClubMemberMapper;
import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.repository.ClubMemberRepository;
import org.springframework.stereotype.Service;
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

        try {
            return clubMemberCatalog.deactivateClubMemberById(clubMember.getId());
        } catch (Exception e) {
            // Logger l'erreur correctement
            System.err.println("Erreur lors de la désactivation du membre : " + e.getMessage());
            return 0;
        }
    }
}
