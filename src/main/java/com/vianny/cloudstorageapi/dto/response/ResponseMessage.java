package com.vianny.cloudstorageapi.dto.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResponseMessage {
    private HttpStatus httpStatus;
    private String message;
    private final LocalDateTime dateTime = LocalDateTime.now();

    public ResponseMessage(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
