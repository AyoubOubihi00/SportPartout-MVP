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
public class UserInput {
    private UUID userId;
    private String firstName;
    private String lastName;
    private OffsetDateTime birthDate;
    private String email;
    private String phoneNumber;


    public Map<String, Object> toGraphqlMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("userId", userId != null ? userId.toString() : null);
        m.put("firstName", firstName);
        m.put("lastName", lastName);
        // Ensure DateTime is sent as ISO-8601 string for GraphQL variables
        m.put("birthDate", birthDate != null ? birthDate.toString() : null);
        m.put("email", email);
        if (phoneNumber != null) {
            // Keep as string to avoid numeric coercion (e.g., leading zero loss)
            m.put("phoneNumber", phoneNumber);
        }
        return m;
    }
}
