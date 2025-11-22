package fr.monclub.planning_service.planning.service;

import fr.monclub.planning_service.config.RabbitMQ.Events.ClubUpdatedEvent;
import fr.monclub.planning_service.planning.catalog.SeanceCatalog;
import fr.monclub.planning_service.planning.repository.SeanceRepository;
import fr.monclub.planning_service.planning.model.Seance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor

public class UpdateClubService {

    private final SeanceCatalog seanceCatalog;

    @Transactional
    public void apply(ClubUpdatedEvent event) {
        List<Seance> seances = seanceCatalog.findByClubId(event.getClubId());
        for (Seance seance : seances) {
            seance.setClubName(event.getName());
            seance.setNameSport(event.getTypeName());
        }
        seanceCatalog.saveAll(seances);
    }
}

