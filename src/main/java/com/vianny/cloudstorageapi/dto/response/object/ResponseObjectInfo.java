package com.vianny.cloudstorageapi.dto.response.object;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseObjectInfo<T>{
    private HttpStatus httpStatus;
    private T properties;
    private final LocalDateTime dateTime = LocalDateTime.now();

    public ResponseObjectInfo(HttpStatus httpStatus, T properties) {
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

    public String getDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
