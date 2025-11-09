package fr.monclub.club_membership_service.membership.catalog;

import fr.monclub.club_membership_service.membership.model.ClubMember;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface ClubMemberCatalog {

    ClubMember findByClubIdAndUserId(UUID clubId, UUID userId);

    List<ClubMember> findAllByClubId(UUID clubId);
    List<ClubMember> findAllByClubIdAndIsActifTrue(UUID clubId);
    List<ClubMember> findAllByClubIdAndIsActifFalse(UUID clubId);

    boolean deleteClubMemberById(UUID uuid);

    ClubMember save(ClubMember clubMember);

    int deactivateClubMemberById(UUID memberId);


    List<ClubMember> findClubMembersByUserId(UUID userId);

}
