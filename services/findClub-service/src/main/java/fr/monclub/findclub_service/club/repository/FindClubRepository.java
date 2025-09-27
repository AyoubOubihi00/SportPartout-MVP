package fr.monclub.findclub_service.club.repository;

import fr.monclub.findclub_service.club.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FindClubRepository extends JpaRepository<Club, UUID>, FindClubRepositoryCustom {

    @Query("""
            SELECT DISTINCT c FROM Club c
            WHERE (:city IS NULL OR LOWER(TRIM(c.city)) = LOWER(TRIM(COALESCE(:city, ''))))
              AND (:postalCode IS NULL OR TRIM(c.postalCode) = TRIM(COALESCE(:postalCode, '')))
            ORDER BY LOWER(c.name)
            """)
    List<Club> findAllByCityAndPostalCode(@Param("city") String city, @Param("postalCode") String postalCode);
}