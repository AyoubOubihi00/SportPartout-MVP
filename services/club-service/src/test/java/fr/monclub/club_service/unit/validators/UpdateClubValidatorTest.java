package fr.monclub.club_service.unit.validators;

import static org.junit.jupiter.api.Assertions.*;


import java.util.UUID;

import fr.monclub.club_service.core.exception.ValidationException;
import fr.monclub.club_service.club.model.UpdateClubInput;
import fr.monclub.club_service.club.validators.UpdateClubValidator;
import fr.monclub.club_service.fixtures.ClubFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateClubValidatorTest {

    private UpdateClubValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UpdateClubValidator();
    }

    @Test
    void shouldPassValidationWhenClothingIsValid() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setEmail("email@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertDoesNotThrow(() -> validator.validate(updateClubInput));
    }

    @Test
    void shouldThrowExceptionWhenInputIsNull() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = null;
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                        .getErrors()
                        .contains("Input is null"));

    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName(null);
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setEmail("email@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                        .getErrors()
                        .contains("Le champ 'name' ne peut pas être null."));

    }

    @Test
    void shouldThrowExceptionWhenNameIsTooLong() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("This name is way too long and exceeds the maximum length of one hundred characters. It should trigger a validation error.");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                        .getErrors()
                        .contains("Name too long"));

    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail(null);
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                        .getErrors()
                        .contains("Le champ 'email' ne peut pas être null."));

    }

    @Test
    void shouldThrowExceptionWhenEmailIsTooLong() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("This email address is way too long and exceeds the maximum length of one hundred characters. It should trigger a validation");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                        .getErrors()
                        .contains("Email too long"));


    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberIsNull() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber(null);
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                        .getErrors()
                        .contains("Le champ 'phoneNumber' ne peut pas être null."));

    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberIsTooLong() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("This phone number is way too long and exceeds the maximum length of fifteen characters.");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                        .getErrors()
                        .contains("Phone number too long"));

    }

    @Test
    void shouldThrowExceptionWhenCountryIsNull() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry(null);
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                .getErrors()
                .contains("Le champ 'country' ne peut pas être null."));

    }

    @Test
    void shouldThrowExceptionWhenCountryIsTooLong() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("This country name is way too long and exceeds the maximum length of one hundred characters. It should trigger a validation error.");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                .getErrors()
                .contains("Country too long"));


    }

    @Test
    void shouldThrowExceptionWhenLatitudeIsOutOfBounds() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setLatitude(100.0); // Invalid latitude
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                .getErrors()
                .contains("Latitude invalid"));

    }

    @Test
    void shouldThrowExceptionWhenLongitudeIsOutOfBounds() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(200.0); // Invalid longitude
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                .getErrors()
                .contains("Longitude invalid"));

    }

    @Test
    void shouldThrowExceptionWhenTypeIdIsInvalid() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setTypeId(null);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                .getErrors()
                .contains("Le champ 'typeId' ne peut pas être null."));

    }

    @Test
    void shouldThrowExceptionWhenTypeNameIsInvalid() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName(null);
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                .getErrors()
                .contains("Le champ 'typeName' ne peut pas être null."));

    }

    @Test
    void shouldThrowExceptionWhenAddressIsNull() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress(null); // Null address
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                .getErrors()
                .contains("Le champ 'address' ne peut pas être null."));


    }

    @Test
    void shouldThrowExceptionWhenAddressIsTooLong() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("Paris");
        updateClubInput.setAddress("This address is way too long and exceeds the maximum length of two hundred characters. It should trigger a validation error. This address is way too long and exceeds the "
                + " maximum length of two hundred characters. It should trigger a validation error.");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                .getErrors()
                .contains("Address too long"));

    }

    @Test
    void shouldThrowExceptionWhenCityIsNull() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity(null); // Null city
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                .getErrors()
                .contains("Le champ 'city' ne peut pas être null."));


    }

    @Test
    void shouldThrowExceptionWhenCityIsTooLong() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName("Valid Name");
        updateClubInput.setEmail("example@example.com");
        updateClubInput.setPhoneNumber("+33123456789");
        updateClubInput.setTypeId(ClubFixtures.UUID_FOOTBALL);
        updateClubInput.setTypeName("FOOTBALL");
        updateClubInput.setCountry("France");
        updateClubInput.setCity("This city name is way too long and exceeds the maximum length"
                + " of one hundred characters. It should trigger a validation error.");
        updateClubInput.setAddress("123 Main St");
        updateClubInput.setLatitude(48.8566);
        updateClubInput.setLongitude(2.3522);
        // Then
        assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertTrue(validator
                .getErrors()
                .contains("City too long"));

    }

    @Test
    void shouldCollectMultipleErrors() {
        // Given
        UUID tagId = UUID.randomUUID();
        // When
        UpdateClubInput updateClubInput = new UpdateClubInput();
        updateClubInput.setName(null); // Null name
        updateClubInput.setEmail("This email address is way too long and exceeds the maximum length of one hundred characters. It should trigger a validation");
        updateClubInput.setPhoneNumber(null); // Null phone number
        updateClubInput.setTypeId(null);
        updateClubInput.setTypeName(null);        updateClubInput.setCountry(null); // Null country
        updateClubInput.setCity("This city name is way too long and exceeds the maximum length"
                + " of one hundred characters. It should trigger a validation error.");
        updateClubInput.setAddress(null); // Null address
        updateClubInput.setLatitude(100.0); // Invalid latitude
        updateClubInput.setLongitude(200.0); // Invalid longitude
        // Then
        ValidationException exception = assertThrows(ValidationException.class, () -> validator.apply(updateClubInput));
        assertEquals(10, exception.getErrors().size());
        assertTrue(exception.getErrors().contains("Le champ 'name' ne peut pas être null."));
        assertTrue(exception.getErrors().contains("Email too long"));
        assertTrue(exception.getErrors().contains("Le champ 'phoneNumber' ne peut pas être null."));
        assertTrue(exception.getErrors().contains("Le champ 'typeId' ne peut pas être null."));
        assertTrue(exception.getErrors().contains("Le champ 'typeName' ne peut pas être null."));
        assertTrue(exception.getErrors().contains("Le champ 'country' ne peut pas être null."));
        assertTrue(exception.getErrors().contains("City too long"));
        assertTrue(exception.getErrors().contains("Le champ 'address' ne peut pas être null."));
        assertTrue(exception.getErrors().contains("Latitude invalid"));
        assertTrue(exception.getErrors().contains("Longitude invalid"));
    }

}