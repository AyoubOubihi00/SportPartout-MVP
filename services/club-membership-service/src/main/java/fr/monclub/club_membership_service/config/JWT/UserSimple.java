package fr.monclub.club_membership_service.config.JWT;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserSimple {
    private final UUID id;
    private final String email;
}