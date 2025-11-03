package fr.monclub.planning_service.core.exception;

public class NoRegistrationException extends RuntimeException {
    public NoRegistrationException() { super("Aucune inscription à annuler."); }
    public NoRegistrationException(String message) { super(message); }
}

