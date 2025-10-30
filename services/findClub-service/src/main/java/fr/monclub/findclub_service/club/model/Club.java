package fr.monclub.findclub_service.club.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "club")
public class Club {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String name;
    private double notation;
    private int memberCount;

    private UUID typeIdSport;
    private String typeNameSport;

    private String city;
    private String country;
    private String address;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;

    private double latitude;
    private double longitude;

    @Column(name = "owner_id")
    private UUID ownerId;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;



}
