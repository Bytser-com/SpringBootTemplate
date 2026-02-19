package com.bytser.template.dtos.requests;

import jakarta.validation.constraints.Email;

public class UpdateExampleRequest {

    private final String username;

    @Email(message = "Ongeldig emailadres")
    private final String email;

    private final String password;

    public UpdateExampleRequest(
            String username,
            String email,
            String password
    ) {
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
