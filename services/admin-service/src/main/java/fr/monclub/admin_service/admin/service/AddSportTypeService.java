package fr.monclub.admin_service.admin.service;

import fr.monclub.admin_service.admin.catalog.SportTypeCatalog;
import fr.monclub.admin_service.admin.model.SportType;
import fr.monclub.admin_service.admin.validators.UpdateSportTypeValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AddSportTypeService {

    private final SportTypeCatalog sportTypeCatalog;
    private final UpdateSportTypeValidator updateSportTypeValidator;

    public SportType apply(String name) {
        updateSportTypeValidator.apply(name);
        return sportTypeCatalog.save(new SportType(name));
    }

}
