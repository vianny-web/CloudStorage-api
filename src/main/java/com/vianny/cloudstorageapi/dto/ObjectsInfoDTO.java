package com.vianny.cloudstorageapi.dto;

import com.vianny.cloudstorageapi.enums.TypeObject;

public class ObjectsInfoDTO {
    private String objectName;
    private TypeObject objectType;

    public ObjectsInfoDTO(String objectName, TypeObject objectType) {
        this.objectName = objectName;
        this.objectType = objectType;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public TypeObject getObjectType() {
        return objectType;
    }

    public void setObjectType(TypeObject objectType) {
        this.objectType = objectType;
    }
}
