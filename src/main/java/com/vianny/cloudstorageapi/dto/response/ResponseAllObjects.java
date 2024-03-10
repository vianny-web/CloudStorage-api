package com.vianny.cloudstorageapi.dto.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResponseAllObjects<T> {
    private HttpStatus httpStatus;
    private T objects;
    private final LocalDateTime dateTime = LocalDateTime.now();

    public ResponseAllObjects(HttpStatus httpStatus, T objects) {
        this.httpStatus = httpStatus;
        this.objects = objects;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public T getObjects() {
        return objects;
    }

    public void setObjects(T objects) {
        this.objects = objects;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
