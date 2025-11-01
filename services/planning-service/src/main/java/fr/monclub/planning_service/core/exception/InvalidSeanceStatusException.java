package fr.monclub.planning_service.core.exception;

import fr.monclub.planning_service.planning.model.SeanceStatus;

public class InvalidSeanceStatusException extends RuntimeException {
    public InvalidSeanceStatusException(SeanceStatus currentStatus, SeanceStatus expectedStatus) {
        super(String.format("Le statut de la séance doit être %s mais il est %s", expectedStatus, currentStatus));
    }
}
