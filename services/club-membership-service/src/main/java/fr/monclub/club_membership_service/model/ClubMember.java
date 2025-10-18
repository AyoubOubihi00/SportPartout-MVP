package fr.monclub.club_membership_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.*;


import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClubMember {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private UUID clubId;

    private UUID userId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;

    private Boolean isActif = true;
    private LocalDate dateSubscription = LocalDate.now();
    private Boolean himself = false;

}
