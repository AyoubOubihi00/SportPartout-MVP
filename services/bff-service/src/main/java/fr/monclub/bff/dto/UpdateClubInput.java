package fr.monclub.bff.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClubInput {
    private String name;
    private UUID typeId;
    private String typeName;
    private String city;
    private String address;
    private String country;
    private String phoneNumber;
    private String postalCode;
    private String email;
    private Float latitude;
    private Float longitude;
    private OffsetDateTime updatedAt;

    public Map<String, Object> toGraphqlMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("typeId", typeId != null ? typeId.toString() : null);
        m.put("typeName", typeName);
        m.put("city", city);
        m.put("address", address);
        m.put("country", country);
        m.put("phoneNumber", phoneNumber);
        m.put("postalCode", postalCode);
        m.put("email", email);
        m.put("latitude", latitude);
        m.put("longitude", longitude);
        m.put("updatedAt", updatedAt != null ? updatedAt.toString() : null);
        return m;
    }
}
