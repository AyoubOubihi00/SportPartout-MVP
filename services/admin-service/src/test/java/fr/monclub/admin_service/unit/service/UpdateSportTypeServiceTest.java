package fr.monclub.admin_service.unit.service;

import fr.monclub.admin_service.admin.catalog.SportTypeCatalog;
import fr.monclub.admin_service.admin.model.SportType;
import fr.monclub.admin_service.admin.service.AddSportTypeService;
import fr.monclub.admin_service.admin.service.UpdateSportTypeService;
import fr.monclub.admin_service.admin.validators.UpdateSportTypeValidator;
import fr.monclub.admin_service.config.RabbitMQ.Events.SportTypeEvent;
import fr.monclub.admin_service.core.exception.TypeSportNotFoundException;
import fr.monclub.admin_service.core.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static graphql.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class UpdateSportTypeServiceTest {

    @Mock
    private SportTypeCatalog catalog;

    private UpdateSportTypeService updateSportTypeService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UpdateSportTypeValidator validator;

    @BeforeEach
    void setUp() {
        updateSportTypeService = new UpdateSportTypeService(catalog, validator,rabbitTemplate);
    }

    @Test
    void apply_shouldUpdateSportType_whenDataIsValid() {
        UUID sportTypeId = UUID.randomUUID();
        String newName = "Updated Sport";

        SportType existingSportType = new SportType();
        existingSportType.setId(sportTypeId);
        existingSportType.setName("Old Sport");

        when(catalog.getSportTypeById(sportTypeId)).thenReturn(existingSportType);
        when(catalog.save(any(SportType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SportType updatedSportType = updateSportTypeService.apply(sportTypeId, newName);

        verify(catalog).getSportTypeById(sportTypeId);
        verify(catalog).save(existingSportType);
        verify(rabbitTemplate).convertAndSend(
                eq("monclub.events"),
                eq("sporttype.updated"),
                argThat((Object event) -> {
                    assertTrue(event instanceof SportTypeEvent);
                    SportTypeEvent e = (SportTypeEvent) event;
                    return e.getUuid().equals(sportTypeId) && e.getName().equals(newName);
                })
        );



        assert updatedSportType.getName().equals(newName);
    }

    @Test
    void apply_shouldThrowValidationException_whenNameIsEmpty() {
        UUID sportTypeId = UUID.randomUUID();
        String newName = "";

        SportType existingSportType = new SportType();
        existingSportType.setId(sportTypeId);
        existingSportType.setName("Old Sport");

        when(catalog.getSportTypeById(sportTypeId)).thenReturn(existingSportType);

        assertThrows(ValidationException.class, () -> {
            updateSportTypeService.apply(sportTypeId, newName);
        });

        verify(catalog).getSportTypeById(sportTypeId);
        verify(catalog, never()).save(any(SportType.class));
    }

    @Test
    void apply_shouldThrowValidationException_whenNameIsNull() {
        UUID sportTypeId = UUID.randomUUID();
        String newName = null;

        SportType existingSportType = new SportType();
        existingSportType.setId(sportTypeId);
        existingSportType.setName("Old Sport");

        when(catalog.getSportTypeById(sportTypeId)).thenReturn(existingSportType);

        assertThrows(ValidationException.class, () -> {
            updateSportTypeService.apply(sportTypeId, newName);
        });

        verify(catalog).getSportTypeById(sportTypeId);
        verify(catalog, never()).save(any(SportType.class));
    }

}