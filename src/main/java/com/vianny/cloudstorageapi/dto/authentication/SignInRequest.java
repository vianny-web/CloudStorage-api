package com.vianny.cloudstorageapi.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignInRequest {
    @NotBlank
    @Size(min = 4, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String login;
    @NotBlank
    @Size(min = 6, max = 25)
    @Pattern(regexp = "^[a-zA-Z0-9\\p{Punct}]*$")
    private String password;

    public SignInRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
