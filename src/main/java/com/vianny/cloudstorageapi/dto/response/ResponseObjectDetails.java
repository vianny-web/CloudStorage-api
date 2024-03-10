package com.vianny.cloudstorageapi.dto.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResponseObjectDetails<T>{
    private HttpStatus httpStatus;
    private T properties;
    private final LocalDateTime dateTime = LocalDateTime.now();

    public ResponseObjectDetails(HttpStatus httpStatus, T properties) {
        this.httpStatus = httpStatus;
        this.properties = properties;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public T getProperties() {
        return properties;
    }

    public void setProperties(T properties) {
        this.properties = properties;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
