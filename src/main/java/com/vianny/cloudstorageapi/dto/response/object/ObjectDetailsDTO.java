package com.vianny.cloudstorageapi.dto.response.object;

import com.vianny.cloudstorageapi.enums.TypeObject;

import java.time.LocalDateTime;

public class ObjectDetailsDTO {
    private String objectName;
    private Enum<TypeObject> objectType;
    private String objectLocation;
    private int objectSize;
    private LocalDateTime uploadDate;

    public ObjectDetailsDTO(String objectName, Enum<TypeObject> objectType, String objectLocation, int objectSize, LocalDateTime uploadDate) {
        this.objectName = objectName;
        this.objectType = objectType;
        this.objectLocation = objectLocation;
        this.objectSize = objectSize;
        this.uploadDate = uploadDate;
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

    public int getObjectSize() {
        return objectSize;
    }

    public void setObjectSize(int objectSize) {
        this.objectSize = objectSize;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Enum<TypeObject> getObjectType() {
        return objectType;
    }

    public void setObjectType(Enum<TypeObject> objectType) {
        this.objectType = objectType;
    }
}
