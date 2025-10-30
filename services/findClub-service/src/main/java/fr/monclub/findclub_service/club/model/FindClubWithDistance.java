package fr.monclub.findclub_service.club.model;

import fr.monclub.findclub_service.club.model.Club;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindClubWithDistance {

    private final UUID id;
    private final String name;
    private final double notation;
    private int memberCount;
    private final UUID typeIdSport;
    private final String typeNameSport;
    private final String city;
    private final String country;
    private final String address;
    private final String postalCode;
    private final String phoneNumber;
    private final String email;
    private final double latitude;
    private final double longitude;
    private final double distanceKm;

    public FindClubWithDistance(Club club, double distanceKm) {
        this(
                club.getId(),
                club.getName(),
                club.getNotation(),
                club.getMemberCount(),
                club.getTypeIdSport(),
                club.getTypeNameSport(),
                club.getCity(),
                club.getCountry(),
                club.getAddress(),
                club.getPostalCode(),
                club.getPhoneNumber(),
                club.getEmail(),
                club.getLatitude(),
                club.getLongitude(),
                distanceKm
        );
    }
}
