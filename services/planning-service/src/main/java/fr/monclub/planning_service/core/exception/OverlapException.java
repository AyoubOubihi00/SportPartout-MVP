package fr.monclub.planning_service.core.exception;

import java.util.UUID;
import java.time.OffsetDateTime;

public class OverlapException extends RuntimeException {
    public OverlapException(UUID terrainId, OffsetDateTime start, OffsetDateTime end) {
        super("Chevauchement détecté sur le terrain " + terrainId +
              " entre " + start + " et " + end + ".");
    }
    public OverlapException(String message) { super(message); }
}
