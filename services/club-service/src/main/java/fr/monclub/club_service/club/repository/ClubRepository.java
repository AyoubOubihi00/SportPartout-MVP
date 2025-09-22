package fr.monclub.club_service.club.repository;

import fr.monclub.club_service.club.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClubRepository extends JpaRepository<Club, UUID> {
    List<Club> findClubsByTypeId(UUID typeId);
}
