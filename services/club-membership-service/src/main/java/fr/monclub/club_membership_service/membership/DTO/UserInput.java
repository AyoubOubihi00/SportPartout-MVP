package fr.monclub.club_membership_service.membership.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;

import java.time.LocalDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {
    private UUID userId;
    private String firstName;
    private String lastName;
    private OffsetDateTime birthDate;
    private String email;
    private String phoneNumber;
}