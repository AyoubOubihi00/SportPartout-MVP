package fr.monclub.club_membership_service.membership.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClubMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID clubId;

    private UUID userId;
    private String firstName;
    private String lastName;
    private OffsetDateTime birthDate;
    private String email;
    private String phoneNumber;

    private String clubName;
    private String clubAdresse;
    private String clubCity;

    // Extended club snapshot fields to keep a full history
    private UUID clubOwnerId;
    private UUID clubTypeId;
    private String clubTypeName;
    private String clubCountry;
    private String clubPostalCode;
    private String clubPhoneNumber;
    private String clubEmail;
    private Double clubLatitude;
    private Double clubLongitude;
    private OffsetDateTime clubCreatedAt;
    private OffsetDateTime clubUpdatedAt;
    private OffsetDateTime clubDeletedAt;

    private Boolean isActif = true;
    private OffsetDateTime dateSubscription = OffsetDateTime.now();
    private Boolean himself = false;

}
