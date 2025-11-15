package fr.monclub.club_membership_service.RabbitMQ;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClubReactivedEvent {
    private UUID clubId;
    private boolean active;
}
