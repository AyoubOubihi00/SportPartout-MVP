package fr.monclub.auth_service.auth.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UpdateUserInput {
    private String lastName;
    private String firstName;
    private OffsetDateTime birthDate;
    private String phone;
}
