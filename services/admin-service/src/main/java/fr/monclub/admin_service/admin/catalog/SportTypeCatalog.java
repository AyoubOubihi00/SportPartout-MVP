package fr.monclub.admin_service.admin.catalog;


import fr.monclub.admin_service.admin.model.SportType;

import java.util.List;
import java.util.UUID;


public interface SportTypeCatalog {

    List<SportType> getAllSportTypes();

    SportType getSportTypeById(UUID id);

    SportType save(SportType sportType);
}
