package fr.monclub.findclub_service.club.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "club")
public class Club {

    @Id
    private UUID id;
    private String name;
    private double notation;
    private int memberCount;

    private UUID typeIdSport;
    private String typeNameSport;

    private String city;
    private String country;
    private String address;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;

    private double latitude;
    private double longitude;

    @Column(name = "owner_id")
    private UUID ownerId;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
    private boolean isActive = true;



    @PrePersist
    public void ensureIdExists() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

}
