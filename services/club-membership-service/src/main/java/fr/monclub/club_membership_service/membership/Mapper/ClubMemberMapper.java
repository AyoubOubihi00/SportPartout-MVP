package fr.monclub.club_membership_service.membership.Mapper;

import fr.monclub.club_membership_service.membership.DTO.UserInput;
import fr.monclub.club_membership_service.membership.DTO.ClubInput;
import fr.monclub.club_membership_service.membership.model.ClubMember;
import org.mapstruct.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ClubMemberMapper {

    // Let JPA generate entity id; do not map from ClubInput
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userInput.userId")
    @Mapping(target = "clubId", source = "clubId")
    @Mapping(target = "firstName", source = "userInput.firstName")
    @Mapping(target = "lastName", source = "userInput.lastName")
    @Mapping(target = "email", source = "userInput.email")
    @Mapping(target = "birthDate", source = "userInput.birthDate")
    @Mapping(target = "phoneNumber", source = "userInput.phoneNumber")
    // Map snapshot fields from full club input
    @Mapping(target = "clubName", source = "club.name")
    @Mapping(target = "clubAdresse", source = "club.address")
    @Mapping(target = "clubCity", source = "club.city")
    @Mapping(target = "clubOwnerId", source = "club.ownerId")
    @Mapping(target = "clubTypeId", source = "club.typeId")
    @Mapping(target = "clubTypeName", source = "club.typeName")
    @Mapping(target = "clubCountry", source = "club.country")
    @Mapping(target = "clubPostalCode", source = "club.postalCode")
    @Mapping(target = "clubPhoneNumber", source = "club.phoneNumber")
    @Mapping(target = "clubEmail", source = "club.email")
    @Mapping(target = "clubLatitude", source = "club.latitude")
    @Mapping(target = "clubLongitude", source = "club.longitude")
    @Mapping(target = "clubCreatedAt", source = "club.createdAt")
    @Mapping(target = "clubUpdatedAt", source = "club.updatedAt")
    @Mapping(target = "clubDeletedAt", source = "club.deletedAt")
    @BeanMapping(ignoreByDefault = true)
    ClubMember toClubMemberEntity(UUID clubId, UserInput userInput, ClubInput club);

    // Backward-compatible overload for existing tests/usages without club snapshot
    default ClubMember toClubMemberEntity(UUID clubId, UserInput userInput) {
        return toClubMemberEntity(clubId, userInput, new ClubInput());
    }

    


}