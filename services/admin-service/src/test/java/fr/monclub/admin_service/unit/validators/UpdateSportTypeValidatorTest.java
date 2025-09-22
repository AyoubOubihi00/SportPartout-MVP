package fr.monclub.admin_service.unit.validators;

import fr.monclub.admin_service.admin.validators.UpdateSportTypeValidator;

import fr.monclub.admin_service.core.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UpdateSportTypeValidatorTest {

    private UpdateSportTypeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UpdateSportTypeValidator();
    }

    @Test
    void shouldPassValidationWhenClothingIsValid() {
        // When
        String validName = "Football américain";
        // Then
        assertDoesNotThrow(() -> validator.apply(validName));
    }

    @Test
    void shouldThrowExceptionWhenInputIsNull() {
        // When
        String invalidName = null;
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(invalidName));
        System.out.println(validator.getErrors());
        assertTrue(validator.getErrors().contains("Le champ 'name' ne peut pas être null."));
    }

    @Test
    void shouldThrowExceptionWhenInputIsEmpty() {
        // When
        String invalidName = "   ";
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(invalidName));
        System.out.println(validator.getErrors());
        assertTrue(validator.getErrors().contains("Le nom du club ne peut pas être vide."));
    }

    @Test
    void shouldThrowExceptionWhenInputHasInvalidCharacters() {
        // When
        String invalidName = "Football@123";
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(invalidName));
        System.out.println(validator.getErrors());
        assertTrue(validator.getErrors().contains("Le nom du club ne peut contenir que des lettres, des chiffres, des espaces, des apostrophes, des virgules et des tirets."));
    }

    @Test
    void shouldThrowExceptionWhenInputIsTooShort() {
        // When
        String invalidName = "A";
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(invalidName));
        System.out.println(validator.getErrors());
        assertTrue(validator.getErrors().contains("Le nom du club doit contenir entre 2 et 100 caractères."));
    }

    @Test
    void shouldThrowExceptionWhenInputIsTooLong() {
        // When
        String invalidName = "A".repeat(101);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(invalidName));
        System.out.println(validator.getErrors());
        assertTrue(validator.getErrors().contains("Le nom du club doit contenir entre 2 et 100 caractères."));

    }


}