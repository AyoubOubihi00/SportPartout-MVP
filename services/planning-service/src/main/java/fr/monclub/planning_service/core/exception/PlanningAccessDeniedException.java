package fr.monclub.planning_service.core.exception;

public class PlanningAccessDeniedException extends RuntimeException {
    public PlanningAccessDeniedException(String message) {
        super(message);
    }
}
