package fr.monclub.planning_service.planning.repository;

import fr.monclub.planning_service.planning.model.Seance;
import fr.monclub.planning_service.planning.model.SeanceStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SeanceRepositoryImpl implements SeanceRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Seance> search(UUID idClub,
                               UUID idCoach,
                               UUID idTerrain,
                               OffsetDateTime from,
                               OffsetDateTime to,
                               SeanceStatus status) {
        var cb = em.getCriteriaBuilder();
        CriteriaQuery<Seance> cq = cb.createQuery(Seance.class);
        Root<Seance> root = cq.from(Seance.class);

        List<Predicate> predicates = new ArrayList<>();

        // club (obligatoire dans la plupart des cas métier)
        if (idClub != null) {
            predicates.add(cb.equal(root.get("idClub"), idClub));
        }
        if (idCoach != null) {
            predicates.add(cb.equal(root.get("idCoach"), idCoach));
        }
        if (idTerrain != null) {
            predicates.add(cb.equal(root.get("idTerrain"), idTerrain));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        if (from != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), from));
        }
        if (to != null) {
            predicates.add(cb.lessThan(root.get("startTime"), to));
        }

        cq.select(root)
          .where(predicates.toArray(new Predicate[0]))
          .orderBy(cb.asc(root.get("startTime")));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public boolean hasOverlap(UUID idClub,
                              UUID idTerrain,
                              OffsetDateTime start,
                              OffsetDateTime end,
                              UUID excludeId) {
        var cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Seance> root = cq.from(Seance.class);

        List<Predicate> preds = new ArrayList<>();
        preds.add(cb.equal(root.get("idClub"), idClub));
        preds.add(cb.equal(root.get("idTerrain"), idTerrain));
        // statut non annulé
        preds.add(cb.notEqual(root.get("status"), SeanceStatus.CANCELED));
        // overlap (start < end2 && end > start2)
        preds.add(cb.lessThan(root.get("startTime"), end));
        preds.add(cb.greaterThan(root.get("endTime"), start));
        if (excludeId != null) {
            preds.add(cb.notEqual(root.get("id"), excludeId));
        }

        cq.select(cb.count(root)).where(preds.toArray(new Predicate[0]));
        Long count = em.createQuery(cq).getSingleResult();
        return count != null && count > 0;
    }
}
