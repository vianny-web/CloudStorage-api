package com.vianny.cloudstorageapi.dto.response.object;

import com.vianny.cloudstorageapi.enums.TypeObject;

public class ObjectInfoMiniDTO {
    private String objectName;
    private TypeObject objectType;

    public ObjectInfoMiniDTO(String objectName, TypeObject objectType) {
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
