package fr.monclub.club_membership_service.membership.service;

import fr.monclub.club_membership_service.membership.DTO.UserInput;
import fr.monclub.club_membership_service.membership.DTO.ClubInput;
import fr.monclub.club_membership_service.membership.Mapper.ClubMemberMapper;
import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SubscribeMemberService {

    private final ClubMemberCatalog clubMemberCatalog;
    private final ClubMemberMapper clubMemberMapper;

    public SubscribeMemberService(ClubMemberCatalog clubMemberCatalog, ClubMemberMapper mapper) {
        this.clubMemberCatalog = clubMemberCatalog;
        this.clubMemberMapper = mapper;
    }


    //Adheration au club
    public ClubMember subscribe(UUID clubId, UserInput userInput, ClubInput club)
    {
        System.out.println("SubscribeMemberService: subscribe called with clubId=" + clubId + " and userId=" + userInput.getUserId());
        ClubMember existingMember = clubMemberCatalog.findByClubIdAndUserId(clubId, userInput.getUserId());
        if (existingMember != null)
        {
            return null;
        }
            ClubMember newMember = clubMemberMapper.toClubMemberEntity(clubId, userInput, club);
        newMember.setDateSubscription(OffsetDateTime.now());
        try
        {
            return clubMemberCatalog.save(newMember);
        }
        catch (Exception e)
        {
            System.out.println("LAAA ERROR ");
            System.err.println("Erreur lors de la sauvegarde du membre : " + e.getMessage());
            return null;
        }
    }

        // Backward compatible overload for existing tests/usages
        public ClubMember subscribe(UUID clubId, UserInput userInput) {
            // Use no-args ClubInput for backward compatibility; mapper will handle null snapshot fields
            return subscribe(clubId, userInput, new ClubInput());
        }




}