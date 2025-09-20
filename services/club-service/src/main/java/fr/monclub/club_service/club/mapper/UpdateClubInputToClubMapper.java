package fr.monclub.club_service.club.mapper;

import fr.monclub.club_service.club.model.Club;
import fr.monclub.club_service.club.model.UpdateClubInput;
import org.mapstruct.*;

import java.util.function.Function;


@Mapper(componentModel = "spring")
public interface UpdateClubInputToClubMapper extends Function<UpdateClubInput, Club> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    Club apply(UpdateClubInput updateClubInput);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Club updateClub(UpdateClubInput updateClubInput, @MappingTarget Club club);
}
