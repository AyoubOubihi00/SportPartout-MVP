package fr.monclub.bff.exception;

public class ForbiddenOperationException extends BffException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
