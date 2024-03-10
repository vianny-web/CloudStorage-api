package com.vianny.cloudstorageapi.dto;

import java.time.LocalDateTime;

public class ObjectDetailsDTO {
    private String objectName;
    private String objectLocation;
    private int objectSize;
    private LocalDateTime uploadDate;

    public ObjectDetailsDTO(String objectName, String objectLocation, int objectSize, LocalDateTime uploadDate) {
        this.objectName = objectName;
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
}
