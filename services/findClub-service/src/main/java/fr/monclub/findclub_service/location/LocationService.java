package fr.monclub.findclub_service.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private static final String COMMUNE_ENDPOINT = "https://geo.api.gouv.fr/communes";
    private static final String REVERSE_ENDPOINT = "https://api-adresse.data.gouv.fr/reverse/";
    private static final String COUNTRY = "France";

    private final RestTemplate restTemplate;

    public LocationService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(3))
                .build();
    }

    public List<LocationSuggestion> suggestLocations(String term) {
        if (term == null || term.trim().length() < 2) {
            return List.of();
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(COMMUNE_ENDPOINT)
                .queryParam("nom", term.trim())
                .queryParam("fields", "nom,codesPostaux,departement,centre")
                .queryParam("boost", "population")
                .queryParam("limit", 5)
                .build()
                .toUri();

        ResponseEntity<List<GeoCommune>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        List<GeoCommune> communes = response.getBody();
        if (communes == null) {
            return List.of();
        }

        List<LocationSuggestion> suggestions = new ArrayList<>();
        for (GeoCommune commune : communes) {
            if (CollectionUtils.isEmpty(commune.codesPostaux())) {
                suggestions.add(toSuggestion(commune, null));
            } else {
                for (String postalCode : commune.codesPostaux()) {
                    suggestions.add(toSuggestion(commune, postalCode));
                }
            }
        }

        return suggestions;
    }

    public Optional<LocationSuggestion> reverseGeocode(double latitude, double longitude) {
        URI uri = UriComponentsBuilder.fromHttpUrl(REVERSE_ENDPOINT)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("limit", 1)
                .build()
                .toUri();

        ReverseResponse response = restTemplate.getForObject(uri, ReverseResponse.class);
        if (response == null || CollectionUtils.isEmpty(response.features())) {
            return Optional.empty();
        }

        ReverseFeature feature = response.features().get(0);
        ReverseProperties properties = feature.properties();
        if (properties == null) {
            return Optional.empty();
        }

        String city = firstNonBlank(properties.city(), properties.town(), properties.village(), properties.municipality());
        String postalCode = properties.postcode();
        String department = extractDepartment(properties.context());

        return Optional.of(buildSuggestion(city, postalCode, department, latitude, longitude));
    }

    private LocationSuggestion toSuggestion(GeoCommune commune, String postalCode) {
        Double latitude = null;
        Double longitude = null;
        if (commune.centre() != null && !CollectionUtils.isEmpty(commune.centre().coordinates())) {
            List<Double> coordinates = commune.centre().coordinates();
            if (coordinates.size() >= 2) {
                longitude = coordinates.get(0);
                latitude = coordinates.get(1);
            }
        }

        String department = "";
        if (commune.departement() != null) {
            department = safeString(commune.departement().nom());
        }

        return buildSuggestion(commune.nom(), postalCode, department, latitude, longitude);
    }

    private LocationSuggestion buildSuggestion(String city, String postalCode, String department, Double latitude, Double longitude) {
        String safeCity = safeString(city);
        String safeDepartment = safeString(department);
        String safePostalCode = safeString(postalCode);

        String label = formatLabel(safeCity, safePostalCode, safeDepartment, COUNTRY);

        return new LocationSuggestion(
                label,
                safeCity,
                safePostalCode,
                safeDepartment,
                COUNTRY,
                latitude,
                longitude
        );
    }

    private String formatLabel(String city, String postalCode, String department, String country) {
        StringBuilder builder = new StringBuilder();
        if (!city.isBlank()) {
            builder.append(city);
        }

        if (!postalCode.isBlank()) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append("(").append(postalCode).append(")");
        }

        if (!department.isBlank()) {
            if (builder.length() > 0) {
                builder.append(" - ");
            }
            builder.append(department);
        }

        if (!country.isBlank()) {
            if (builder.length() > 0) {
                builder.append(" - ");
            }
            builder.append(country);
        }

        return builder.toString();
    }

    private String extractDepartment(String context) {
        if (context == null) {
            return "";
        }

        String[] parts = context.split(",");
        if (parts.length >= 2) {
            return parts[1].trim();
        }
        return context.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeoCommune(
            String nom,
            List<String> codesPostaux,
            GeoDepartement departement,
            GeoPoint centre
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeoDepartement(String nom) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeoPoint(List<Double> coordinates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ReverseResponse(List<ReverseFeature> features) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ReverseFeature(ReverseProperties properties) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ReverseProperties(
            String city,
            String town,
            String village,
            String municipality,
            String postcode,
            String context
    ) {
    }
}
