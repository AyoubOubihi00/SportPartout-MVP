package fr.monclub.planning_service.core.exception;

public class InvalidDatesException extends RuntimeException {
    public InvalidDatesException() { super("Dates invalides (start >= end ou intervalle vide)."); }
    public InvalidDatesException(String message) { super(message); }
}
