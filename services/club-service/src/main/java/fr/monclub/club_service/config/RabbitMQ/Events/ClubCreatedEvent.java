package fr.monclub.club_service.config.RabbitMQ.Events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClubCreatedEvent implements Serializable {
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
    private boolean isActive;
}
