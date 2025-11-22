package fr.monclub.club_membership_service.RabbitMQ;

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
public class UserUpdatedEvent implements Serializable {
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isActive;
    private String phone;
    private String profilePictureUrl;
    private String bio;
    private OffsetDateTime birthDate;
}
