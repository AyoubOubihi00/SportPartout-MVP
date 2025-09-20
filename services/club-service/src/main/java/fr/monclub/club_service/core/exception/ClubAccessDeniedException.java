package fr.monclub.club_service.core.exception;

public class ClubAccessDeniedException extends RuntimeException {
    public ClubAccessDeniedException(String message) {
        super(message);
    }
}
