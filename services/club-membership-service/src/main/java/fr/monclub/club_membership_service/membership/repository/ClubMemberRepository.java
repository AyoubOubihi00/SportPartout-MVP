package fr.monclub.club_membership_service.membership.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import fr.monclub.club_membership_service.membership.model.ClubMember;

import java.util.List;
import java.util.UUID;

public interface ClubMemberRepository extends JpaRepository<ClubMember, UUID> {
    ClubMember findByClubIdAndUserId(UUID clubId, UUID userId);


    List<ClubMember> findAllByClubId(UUID clubId);
    List<ClubMember> findAllByClubIdAndIsActifTrue(UUID clubId);
    List<ClubMember> findAllByClubIdAndIsActifFalse(UUID clubId);


    @Modifying
    @Transactional
    @Query("UPDATE ClubMember c SET c.isActif = false, c.dateSubscription = null, c.himself = false WHERE c.id = :memberId")
    int deactivateClubMemberById(UUID memberId);

    List<ClubMember> findAllByUserId(UUID userId);
    List<ClubMember> findClubMembersByUserId(UUID userId);



}
