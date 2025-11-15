package fr.monclub.findclub_service.RabbitMQ.listener;

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
public class ClubUpdatedEvent implements Serializable {
    private UUID clubId;
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
    private OffsetDateTime updatedAt;
}