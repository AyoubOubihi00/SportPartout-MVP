package fr.monclub.admin_service.admin.validators;


import fr.monclub.admin_service.core.validator.BaseValidator;
import fr.monclub.admin_service.core.validator.VoidFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateSportTypeValidator extends BaseValidator<String> {


    @Override
    public VoidFunction validate(String name) {

        if (name == null) {
            addErrorNull("name");
            return null;
        }

        if (name != null && name.isBlank()) {
            addError("Le nom du club ne peut pas être vide.");
        }

        assert name != null;
        if (!name.matches("^[a-zA-Z0-9À-ÿ ',-]+$")) {
            addError("Le nom du club ne peut contenir que des lettres, des chiffres, des espaces, des apostrophes, des virgules et des tirets.");
        }

        if (name.length() < 2 || name.length() > 100) {
            addError("Le nom du club doit contenir entre 2 et 100 caractères.");
        }


        return null;
    }
}