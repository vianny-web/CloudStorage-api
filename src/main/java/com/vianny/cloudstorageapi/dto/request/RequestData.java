package com.vianny.cloudstorageapi.dto.request;

public class RequestData {
    private String objectName;
    private String objectLocation;

    public RequestData(String objectName, String objectLocation) {
        this.objectName = objectName;
        this.objectLocation = objectLocation;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectLocation() {
        return objectLocation;
    }

    public void setObjectLocation(String objectLocation) {
        this.objectLocation = objectLocation;
    }
}
