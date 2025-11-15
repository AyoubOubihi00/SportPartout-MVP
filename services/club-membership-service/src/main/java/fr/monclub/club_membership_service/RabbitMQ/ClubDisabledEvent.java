package fr.monclub.club_membership_service.RabbitMQ;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClubDisabledEvent {
    private UUID clubId;
    private OffsetDateTime disabledAt;
    private boolean active;
}
