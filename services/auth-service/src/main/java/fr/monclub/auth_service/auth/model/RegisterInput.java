package fr.monclub.auth_service.auth.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class RegisterInput{
        private String email;
        private String password;
        private String lastName;
        private String firstName;
        private String phoneNumber;
        private OffsetDateTime birthDate;
        private String phone;
        }
