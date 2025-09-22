package fr.monclub.admin_service.admin.catalog;

import fr.monclub.admin_service.admin.model.SportType;
import fr.monclub.admin_service.core.exception.TypeSportNotFoundException;
import fr.monclub.admin_service.admin.repository.SportTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SportTypeCatalogImpl implements SportTypeCatalog {

    private final SportTypeRepository repository;

    @Override
    public List<SportType> getAllSportTypes() {
        return repository.findAll();
    }

    @Override
    public SportType getSportTypeById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new TypeSportNotFoundException(id));
    }

    @Override
    public SportType save(SportType sportType) {
        return  repository.save(sportType);
    }

}
