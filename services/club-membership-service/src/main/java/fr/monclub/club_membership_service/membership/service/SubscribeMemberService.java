package fr.monclub.club_membership_service.membership.service;

import fr.monclub.club_membership_service.membership.DTO.UserInput;
import fr.monclub.club_membership_service.membership.DTO.ClubInput;
import fr.monclub.club_membership_service.membership.Mapper.ClubMemberMapper;
import fr.monclub.club_membership_service.membership.catalog.ClubMemberCatalog;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import org.springframework.stereotype.Service;

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
            if (Boolean.TRUE.equals(existingMember.getIsActif())) {
                return null;
            }
            reactivateMember(existingMember, userInput, club);
            return saveMember(existingMember);
        }
        ClubMember newMember = clubMemberMapper.toClubMemberEntity(clubId, userInput, club);
        newMember.setDateSubscription(OffsetDateTime.now());
        newMember.setIsActif(true);
        newMember.setHimself(false);
        newMember.setDateUnsubscription(null);

        return saveMember(newMember);
    }

    private void reactivateMember(ClubMember member, UserInput userInput, ClubInput club) {
        member.setFirstName(userInput.getFirstName());
        member.setLastName(userInput.getLastName());
        member.setEmail(userInput.getEmail());
        member.setBirthDate(userInput.getBirthDate());
        member.setPhoneNumber(userInput.getPhoneNumber());
        prepareActivation(member);
        if (club != null) {
            member.setClubName(club.getName());
            member.setClubAdresse(club.getAddress());
            member.setClubCity(club.getCity());
            member.setClubOwnerId(club.getOwnerId());
            member.setClubTypeId(club.getTypeId());
            member.setClubTypeName(club.getTypeName());
            member.setClubCountry(club.getCountry());
            member.setClubPostalCode(club.getPostalCode());
            member.setClubPhoneNumber(club.getPhoneNumber());
            member.setClubEmail(club.getEmail());
            member.setClubLatitude(club.getLatitude());
            member.setClubLongitude(club.getLongitude());
            member.setClubCreatedAt(club.getCreatedAt());
            member.setClubUpdatedAt(club.getUpdatedAt());
            member.setClubDeletedAt(club.getDeletedAt());
        }
    }

    private void prepareActivation(ClubMember member) {
        member.setDateSubscription(OffsetDateTime.now());
        member.setIsActif(true);
        member.setHimself(false);
        member.setDateUnsubscription(null);
    }

    private ClubMember saveMember(ClubMember member) {
        try
        {
            return clubMemberCatalog.save(member);
        }
        catch (Exception e)
        {
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
