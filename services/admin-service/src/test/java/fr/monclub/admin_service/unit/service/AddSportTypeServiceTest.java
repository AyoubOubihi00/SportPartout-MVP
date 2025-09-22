package fr.monclub.admin_service.unit.service;

import fr.monclub.admin_service.admin.catalog.SportTypeCatalog;
import fr.monclub.admin_service.admin.model.SportType;
import fr.monclub.admin_service.admin.service.AddSportTypeService;
import fr.monclub.admin_service.admin.validators.UpdateSportTypeValidator;

import fr.monclub.admin_service.core.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddSportTypeServiceTest {

    @Mock
    private SportTypeCatalog catalog;


    private AddSportTypeService addSportTypeService;

    @Autowired
    private UpdateSportTypeValidator validator;

    @BeforeEach
    void setUp() {
        addSportTypeService = new AddSportTypeService(catalog, validator);
    }

    @Test
    void apply_shouldCreateSportType_whenInputIsValid() {
        String input = "tennis";

        SportType mapped = new SportType();
        mapped.setId(UUID.randomUUID());
        mapped.setName(input);

        when(catalog.save(any(SportType.class))).thenReturn(mapped);
        when(catalog.getSportTypeById(mapped.getId())).thenReturn(mapped);


        SportType updated = addSportTypeService.apply(input);
        SportType res = catalog.getSportTypeById(updated.getId());

        verify(catalog).save(any(SportType.class));
        assert(res.getName().equals(input));
        assert(res.getId().equals(mapped.getId()));
    }

    @Test
    void apply_shouldThrowException_whenNameIsEmpty() {
        String input = "";

        assertThrows(ValidationException.class,
                () -> addSportTypeService.apply(input));

        verify(catalog, never()).save(any(SportType.class));
    }

    @Test
    void apply_shouldThrowException_whenNameIsNull() {
        String input = null;

        assertThrows(ValidationException.class,
                () -> addSportTypeService.apply(input));

        verify(catalog, never()).save(any(SportType.class));
    }




}
