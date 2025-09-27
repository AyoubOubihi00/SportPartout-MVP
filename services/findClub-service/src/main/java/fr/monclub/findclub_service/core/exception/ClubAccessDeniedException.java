package fr.monclub.findclub_service.core.exception;

public class ClubAccessDeniedException extends RuntimeException {
    public ClubAccessDeniedException(String message) {
        super(message);
    }
}

