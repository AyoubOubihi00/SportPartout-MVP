package fr.monclub.club_service.config.RabbitMQ.Events;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClubReactivedEvent implements Serializable {
    private UUID clubId;
    private boolean active;
}
