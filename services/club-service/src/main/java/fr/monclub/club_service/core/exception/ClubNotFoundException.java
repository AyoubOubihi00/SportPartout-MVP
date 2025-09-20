package fr.monclub.club_service.core.exception;

import java.util.UUID;

public class ClubNotFoundException extends RuntimeException {
    public ClubNotFoundException(UUID id) {
        super("Club not found with id: " + id);
    }
}
