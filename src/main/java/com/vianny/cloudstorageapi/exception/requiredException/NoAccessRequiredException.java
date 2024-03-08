package com.vianny.cloudstorageapi.exception.requiredException;

public class NoAccessRequiredException extends RuntimeException {
    public NoAccessRequiredException(String message) {
        super(message);
    }
}
