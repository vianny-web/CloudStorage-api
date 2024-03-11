package com.vianny.cloudstorageapi.exception.requiredException;

public class ServerErrorRequiredException extends RuntimeException {
    public ServerErrorRequiredException(String message) {
        super(message);
    }
}
