package fr.monclub.club_membership_service.catalog;

import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.repository.ClubMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClubMemberCatalogImpl implements ClubMemberCatalog {

    private final ClubMemberRepository clubMemberRepository;

    public ClubMemberCatalogImpl(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }

    public ClubMember findByClubIdAndUserId(UUID clubId, UUID userId)
    {
        return this.clubMemberRepository.findByClubIdAndUserId(clubId, userId);
    }

    public List<ClubMember> findAllByClubId(UUID clubId)
    {

        return this.clubMemberRepository.findAllByClubId(clubId);
    }

    @Override
    public List<ClubMember> findAllByClubIdAndIsActifTrue(UUID clubId) {
        return this.clubMemberRepository.findAllByClubIdAndIsActifTrue(clubId);
    }

    @Override
    public List<ClubMember> findAllByClubIdAndIsActifFalse(UUID clubId) {
        return this.clubMemberRepository.findAllByClubIdAndIsActifFalse(clubId);
    }

    public boolean deleteClubMemberById(UUID uuid) {
        if (this.clubMemberRepository.existsById(uuid))
        {
            this.clubMemberRepository.deleteById(uuid);
            return true;
        }
        return false;
    }

    public ClubMember save(ClubMember clubMember)
    {

        ClubMember m = this.clubMemberRepository.save(clubMember);
        System.out.println("mail du membre enregistré : " + m.getEmail());
        return m;
    }

    @Override
    public int deactivateClubMemberById(UUID memberId) {
        return this.clubMemberRepository.deactivateClubMemberById(memberId);
    }


    public List<ClubMember>  findClubMembersByUserId(UUID userId)
    {
        return this.clubMemberRepository.findClubMembersByUserId(userId);
    }


}
