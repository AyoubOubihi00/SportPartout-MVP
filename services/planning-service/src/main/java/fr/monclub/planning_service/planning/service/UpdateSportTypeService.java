package fr.monclub.planning_service.planning.service;

import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateSportTypeService {

    private final SeanceRepository seanceRepository;

    @Transactional
    public void apply(UUID sportTypeId, String sportTypeName) {
        List<Seance> seances = seanceRepository.findAllByIdSport(sportTypeId);
        if (seances.isEmpty()) {
            return;
        }

        seances.forEach(seance -> seance.setNameSport(sportTypeName));
        seanceRepository.saveAll(seances);
    }
}
