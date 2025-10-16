package fr.monclub.planning_service.core.exception;

import java.util.UUID;

public class SeanceNotFoundException extends RuntimeException {
    public SeanceNotFoundException(UUID id) {
        super("Séance introuvable avec id: " + id);
    }
    public SeanceNotFoundException(String message) { super(message); }
}
