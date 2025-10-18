package fr.monclub.club_membership_service.service;

import fr.monclub.club_membership_service.Mapper.ClubMemberMapper;
import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.repository.ClubMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AllMembersService {

    private final ClubMemberCatalog clubMemberCatalog;


    public AllMembersService(ClubMemberCatalog clubMemberCatalog, ClubMemberMapper mapper) {
        this.clubMemberCatalog = clubMemberCatalog;
    }


    public List<ClubMember> getAllMembersIsActif(UUID clubId) {
        return clubMemberCatalog.findAllByClubIdAndIsActifTrue(clubId);
    }

    public List<ClubMember> getAllMembersNotActif(UUID clubId) {
        return clubMemberCatalog.findAllByClubIdAndIsActifFalse(clubId);
    }


    public List<ClubMember> getMembersByClub(UUID clubId) {

        return clubMemberCatalog.findAllByClubId(clubId);
    }
}

