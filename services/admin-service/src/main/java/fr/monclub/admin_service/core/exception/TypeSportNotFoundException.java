package fr.monclub.admin_service.core.exception;

import java.util.UUID;

public class TypeSportNotFoundException extends RuntimeException {
    public TypeSportNotFoundException(UUID id) {
        super("Sport type not found with id: " + id);
    }
}
