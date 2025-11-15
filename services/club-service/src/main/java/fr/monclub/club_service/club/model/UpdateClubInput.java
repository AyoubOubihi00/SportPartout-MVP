package fr.monclub.club_service.club.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UpdateClubInput {
    private String name;
    private UUID typeId;
    private String typeName;
    private String city;
    private String address;
    private String country;
    private String phoneNumber;
    private String postalCode;
    private String email;
    private double latitude;
    private double longitude;
    private OffsetDateTime updatedAt;
}
