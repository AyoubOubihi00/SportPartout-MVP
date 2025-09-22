package fr.monclub.admin_service.core.validator;

import fr.monclub.admin_service.core.exception.ValidationException;

import java.util.List;
import java.util.function.Function;

public interface ObjectValidator<T> extends Function<T, VoidFunction> {

    List<String> getErrors();

    @Override
    default VoidFunction apply(T t) {
        getErrors().clear();
        validate(t);

        if (!getErrors().isEmpty()) {
            throw new ValidationException(getErrors());
        }

        return null;
    }

    VoidFunction validate(T target);
}