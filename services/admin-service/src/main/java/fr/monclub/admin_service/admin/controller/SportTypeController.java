package fr.monclub.admin_service.admin.controller;

import fr.monclub.admin_service.admin.model.SportType;
import fr.monclub.admin_service.admin.catalog.SportTypeCatalog;
import fr.monclub.admin_service.admin.service.AddSportTypeService;
import fr.monclub.admin_service.admin.service.UpdateSportTypeService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class SportTypeController {

    private final SportTypeCatalog sportTypeCatalog;
    private final AddSportTypeService addSportTypeService;
    private final UpdateSportTypeService updateSportTypeService;

    @QueryMapping
    public List<SportType> sportTypes() {
        return sportTypeCatalog.getAllSportTypes();
    }

    @QueryMapping
    public SportType sportTypeById(@Argument UUID id) {
        return sportTypeCatalog.getSportTypeById(id);
    }

    @MutationMapping
    public SportType createSportType(@Argument String name) {
        return addSportTypeService.apply(name);
    }

    @MutationMapping
    public SportType updateSportType(@Argument UUID id, @Argument String name) {
        return updateSportTypeService.apply(id, name);
    }



}
