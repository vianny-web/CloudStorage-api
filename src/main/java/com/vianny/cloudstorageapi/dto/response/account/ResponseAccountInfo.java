package com.vianny.cloudstorageapi.dto.response.account;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResponseAccountInfo<T> {
    private HttpStatus httpStatus;
    private T details;
    private final LocalDateTime dateTime = LocalDateTime.now();

    public ResponseAccountInfo(HttpStatus httpStatus, T details) {
        this.httpStatus = httpStatus;
        this.details = details;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public T getDetails() {
        return details;
    }

    public void setDetails(T details) {
        this.details = details;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
