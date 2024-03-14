package com.vianny.cloudstorageapi.exception.requiredException;

public class NoStorageSpaceRequiredException extends RuntimeException {
    public NoStorageSpaceRequiredException(String message) {
        super(message);
    }
}
