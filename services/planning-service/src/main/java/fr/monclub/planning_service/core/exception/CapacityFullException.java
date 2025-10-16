package fr.monclub.planning_service.core.exception;

public class CapacityFullException extends RuntimeException {
    public CapacityFullException() { super("Séance complète : plus de places disponibles."); }
    public CapacityFullException(String message) { super(message); }
}
