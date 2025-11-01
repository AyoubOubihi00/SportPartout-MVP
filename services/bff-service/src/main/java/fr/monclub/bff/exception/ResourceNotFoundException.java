package fr.monclub.bff.exception;

public class ResourceNotFoundException extends BffException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
