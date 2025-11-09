package fr.monclub.club_service.club.model;

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
public class Club {

    @Id
    private UUID id;
    private String name;

    private UUID typeId;
    private String typeName;

    private String city;
    private String country;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private String email;

    private double latitude;
    private double longitude;

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
