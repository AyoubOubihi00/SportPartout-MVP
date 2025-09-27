package fr.monclub.findclub_service.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationSuggestion {

    private final String label;
    private final String city;
    private final String postalCode;
    private final String department;
    private final String country;
    private final Double latitude;
    private final Double longitude;
}
