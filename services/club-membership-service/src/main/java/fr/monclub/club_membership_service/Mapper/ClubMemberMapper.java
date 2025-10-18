package fr.monclub.club_membership_service.Mapper;

import fr.monclub.club_membership_service.DTO.UserInput;
import fr.monclub.club_membership_service.model.ClubMember;
import org.mapstruct.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ClubMemberMapper {


    @Mapping(target = "userId", source = "userInput.userId")
    @Mapping(target = "clubId", source = "clubId")
    @Mapping(target = "firstName", source = "userInput.firstName")
    @Mapping(target = "lastName", source = "userInput.lastName")
    @Mapping(target = "email", source = "userInput.email")
    @Mapping(target = "birthDate", source = "userInput.birthDate")
    @Mapping(target = "phoneNumber", source = "userInput.phoneNumber")
    ClubMember toClubMemberEntity(UUID clubId, UserInput userInput);


}