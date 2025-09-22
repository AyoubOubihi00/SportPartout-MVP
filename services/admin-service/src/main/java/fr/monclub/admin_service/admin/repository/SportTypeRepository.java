package fr.monclub.admin_service.admin.repository;


import fr.monclub.admin_service.admin.model.SportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SportTypeRepository extends JpaRepository<SportType, UUID> {
}
