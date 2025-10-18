package fr.monclub.club_membership_service.service;

import fr.monclub.club_membership_service.DTO.UserInput;
import fr.monclub.club_membership_service.Mapper.ClubMemberMapper;
import fr.monclub.club_membership_service.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.model.ClubMember;
import fr.monclub.club_membership_service.repository.ClubMemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public ClubMember subscribe(UUID clubId, UserInput userInput)
    {
        ClubMember existingMember = clubMemberCatalog.findByClubIdAndUserId(clubId, userInput.getUserId());
        if (existingMember != null)
        {
            return null;
        }
        ClubMember newMember = clubMemberMapper.toClubMemberEntity(clubId, userInput);
        newMember.setDateSubscription(LocalDate.now());
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




}