package fr.monclub.auth_service.config.RabbitMQ.Events;

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
public class UserUpdated implements Serializable {
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
