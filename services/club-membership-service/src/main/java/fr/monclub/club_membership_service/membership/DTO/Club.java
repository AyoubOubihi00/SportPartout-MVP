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
public class Club {
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

    public static Club fromInput(ClubInput in) {
        if (in == null) return null;
        return new Club(
                in.getId(),
                in.getOwnerId(),
                in.getName(),
                in.getTypeId(),
                in.getTypeName(),
                in.getCity(),
                in.getAddress(),
                in.getCountry(),
                in.getPostalCode(),
                in.getPhoneNumber(),
                in.getEmail(),
                in.getLatitude(),
                in.getLongitude(),
                in.getCreatedAt(),
                in.getUpdatedAt(),
                in.getDeletedAt()
        );
    }
}
