package fr.monclub.findclub_service.club.controller;

import fr.monclub.findclub_service.club.catalog.FindClubCatalog;
import fr.monclub.findclub_service.club.model.FindClubWithDistance;
import fr.monclub.findclub_service.club.model.Club;
import fr.monclub.findclub_service.location.LocationService;
import fr.monclub.findclub_service.location.LocationSuggestion;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Locale;

@Controller
@AllArgsConstructor
public class FindClubController {

    private final FindClubCatalog clubCatalog;
    private final LocationService locationService;

    @QueryMapping
    public List<Club> clubs() {
        return clubCatalog.getAllClubs();
    }

    @QueryMapping
    public List<Club> clubsByCity(
            @Argument String city,
            @Argument("postalCode") String postalCode
    ) {
        return clubCatalog.getClubsByCityAndPostalCode(city, postalCode);
    }

    @QueryMapping
    public List<FindClubWithDistance> clubsNear(
            @Argument double latitude,
            @Argument double longitude,
            @Argument double radiusKm
    ) {
        return clubCatalog.getClubsNear(latitude, longitude, radiusKm);
    }

    @QueryMapping
    public List<LocationSuggestion> suggestLocations(@Argument String term) {
        return locationService.suggestLocations(term);
    }

    @QueryMapping
    public LocationSuggestion locationFromCoordinates(
            @Argument double latitude,
            @Argument double longitude
    ) {
        return locationService.reverseGeocode(latitude, longitude)
                .orElseGet(() -> new LocationSuggestion(
                        String.format(Locale.FRANCE, "CoordonnÃ©es %.4f, %.4f", latitude, longitude),
                        "",
                        "",
                        "",
                        "France",
                        latitude,
                        longitude
                ));
    }
}
