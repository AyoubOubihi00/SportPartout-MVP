package fr.monclub.club_membership_service.membership.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubInput {
    private UUID id;
    private UUID ownerId;
    private String name;
    private UUID typeId;
    private String typeName;
    private String city;
    private String address;
    private String country;
    private String postalCode;
    private String phoneNumber;
    private String email;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
}
