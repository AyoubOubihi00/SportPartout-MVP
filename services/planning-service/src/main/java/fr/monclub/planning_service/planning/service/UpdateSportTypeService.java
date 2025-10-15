package fr.monclub.planning_service.planning.service;

import fr.monclub.planning_service.planning.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateSportTypeService {

    private final SeanceRepository seanceRepository;

    @Transactional
    public void apply(UUID sportTypeId, String name) {
        if (sportTypeId == null || name == null) {
            return;
        }
        seanceRepository.updateSportTypeName(sportTypeId, name);
    }
}

