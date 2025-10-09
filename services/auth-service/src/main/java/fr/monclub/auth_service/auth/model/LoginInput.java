package fr.monclub.auth_service.auth.model;

import lombok.Data;

@Data
public class LoginInput {
    private String email;
    private String password;
}

