package com.bytser.template.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUserRequest {

    @NotBlank(message = "Gebruikersnaam is verplicht")
    private final String username;

    @NotBlank(message = "Emailadres is verplicht")
    @Email(message = "Ongeldig emailadres")
    private final String email;
    
    @NotBlank(message = "Wachtwoord is verplicht")
    private final String password;

    public CreateUserRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}

