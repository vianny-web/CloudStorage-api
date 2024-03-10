package com.vianny.cloudstorageapi.dto.request.jwt;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class RequestJWT {
    private HttpStatus httpStatus;
    private String jwtToken;
    private final LocalDateTime dateTime = LocalDateTime.now();

    public RequestJWT(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.jwtToken = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
