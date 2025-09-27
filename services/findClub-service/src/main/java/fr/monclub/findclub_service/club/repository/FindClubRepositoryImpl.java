package fr.monclub.findclub_service.club.repository;

import fr.monclub.findclub_service.club.model.FindClubWithDistance;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public class FindClubRepositoryImpl implements FindClubRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FindClubWithDistance> findWithinRadius(double latitude, double longitude, double radiusKm) {
        double r = Math.max(radiusKm, 0d);

        String sql = """
            SELECT c.id,
                   c.name,
                   c.type_id_sport,
                   c.type_name_sport,
                   c.city,
                   c.country,
                   c.address,
                   c.postal_code,
                   c.phone_number,
                   c.email,
                   c.latitude,
                   c.longitude,
                   ST_Distance(
                     ST_Transform(ST_SetSRID(ST_Point(?1, ?2), 4326), 2154),
                     ST_Transform(ST_SetSRID(ST_Point(c.longitude, c.latitude), 4326), 2154)
                   ) / 1000.0 AS distance_km
            FROM club c
            WHERE ST_Distance(
                     ST_Transform(ST_SetSRID(ST_Point(?1, ?2), 4326), 2154),
                     ST_Transform(ST_SetSRID(ST_Point(c.longitude, c.latitude), 4326), 2154)
                   ) <= ?3 * 1000.0
            ORDER BY distance_km ASC
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                // ⚠️ ordre ST_Point(lon, lat)
                .setParameter(1, longitude)
                .setParameter(2, latitude)
                .setParameter(3, r)
                .getResultList();

        return rows.stream().map(this::mapRow).toList();
    }

    @Override
    public List<FindClubWithDistance> findWithinRadius(double latitude, double longitude, double radiusKm, UUID typeSportId, String typeSportName) {
        double r = Math.max(radiusKm, 0d);

        String sql = """
            SELECT c.id,
                   c.name,
                   c.type_id_sport,
                   c.type_name_sport,
                   c.city,
                   c.country,
                   c.address,
                   c.postal_code,
                   c.phone_number,
                   c.email,
                   c.latitude,
                   c.longitude,
                   ST_Distance(
                     ST_Transform(ST_SetSRID(ST_Point(?1, ?2), 4326), 2154),
                     ST_Transform(ST_SetSRID(ST_Point(c.longitude, c.latitude), 4326), 2154)
                   ) / 1000.0 AS distance_km
            FROM club c
            WHERE ST_Distance(
                     ST_Transform(ST_SetSRID(ST_Point(?1, ?2), 4326), 2154),
                     ST_Transform(ST_SetSRID(ST_Point(c.longitude, c.latitude), 4326), 2154)
                   ) <= ?3 * 1000.0
              AND (c.type_id_sport = ?4 AND LOWER(TRIM(c.type_name_sport)) = LOWER(TRIM(COALESCE(?5, ''))))
            ORDER BY distance_km ASC
            """;

        String typeSportNameParam = (typeSportName == null) ? null : typeSportName; // éviter NPE

        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter(1, longitude) // lon
                .setParameter(2, latitude)  // lat
                .setParameter(3, r) // rayon
                .setParameter(4, typeSportId) // id du sport
                .setParameter(5, typeSportNameParam) // nom du sport
                .getResultList();

        return rows.stream().map(this::mapRow).toList();
    }

    private FindClubWithDistance mapRow(Object[] row) {
        return new FindClubWithDistance(
                asUuid(row[0]),
                asString(row[1]),
                asUuid(row[2]),
                asString(row[3]),
                asString(row[4]),
                asString(row[5]),
                asString(row[6]),
                asString(row[7]),
                asString(row[8]),
                asString(row[9]),
                asDouble(row[10]),
                asDouble(row[11]),
                asDouble(row[12])
        );
    }

    private UUID asUuid(Object value) {
        if (value instanceof UUID uuid) return uuid;
        if (value instanceof String s) return UUID.fromString(s);
        return null;
    }

    private String asString(Object value) { return value == null ? null : value.toString(); }

    private double asDouble(Object value) {
        if (value instanceof Double d) return d;
        if (value instanceof BigDecimal dec) return dec.doubleValue();
        if (value instanceof Float f) return f.doubleValue();
        if (value instanceof Number n) return n.doubleValue();
        return 0d;
    }
}
