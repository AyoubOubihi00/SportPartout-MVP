package fr.monclub.club_service.core.validator;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseValidator<T> implements ObjectValidator<T> {

    private final List<String> errors = new ArrayList<>();

    @Override
    public List<String> getErrors() {
        return errors;
    }

    protected void addError(String message) {
        errors.add(message);
    }

    protected void addErrorNull(String field) {
        errors.add("Le champ '" + field + "' ne peut pas être null.");
    }
}