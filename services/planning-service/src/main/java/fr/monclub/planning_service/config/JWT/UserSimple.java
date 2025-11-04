package fr.monclub.planning_service.config.JWT; // Ou dans votre package 'model'

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