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
public class ClubDisabledEvent implements Serializable {
    private UUID clubId;
    private OffsetDateTime disabledAt;
    private boolean active;
}
