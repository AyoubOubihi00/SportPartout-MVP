package fr.monclub.bff.service;

import fr.monclub.bff.exception.ResourceNotFoundException;
import fr.monclub.bff.gateway.ClubGateway;
import fr.monclub.bff.gateway.MembershipGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubListingBffService {

    private final ClubGateway clubGateway;

    public List<ClubSummary> clubsManagedBy(UUID ownerId) {
        List<ClubGateway.ClubDto> clubs = clubGateway.getClubsByOwnerId(ownerId);
        List<ClubSummary> summaries = new ArrayList<>(clubs.size());
        for (ClubGateway.ClubDto club : clubs) {
            if (club.id() != null) {
                summaries.add(ClubSummary.from(club));
            }
        }
        return summaries;
    }

    public record ClubSummary(UUID id,
                              UUID ownerId,
                              String name,
                              UUID typeId,
                              String typeName,
                              String city,
                              String address,
                              String country,
                              String postalCode,
                              String phoneNumber,
                              String email,
                              Float latitude,
                              Float longitude,
                              Boolean isActive
    ) {
        public static ClubSummary from(ClubGateway.ClubDto club) {
            return new ClubSummary(
                    club.id(),
                    club.ownerId(),
                    club.name(),
                    club.typeId(),
                    club.typeName(),
                    club.city(),
                    club.address(),
                    club.country(),
                    club.postalCode(),
                    club.phoneNumber(),
                    club.email(),
                    club.latitude(),
                    club.longitude(),
                    club.isActive()
            );
        }
    }
}
