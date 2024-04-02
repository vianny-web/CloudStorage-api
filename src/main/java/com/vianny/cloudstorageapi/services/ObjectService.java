package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.dto.response.object.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.response.object.ObjectInfoMiniDTO;
import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.repositories.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ObjectService {
    private ObjectRepository objectRepository;
    @Autowired
    public void setObjectRepository(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    @Transactional
    public List<ObjectDetailsDTO> getObjectFromPath(String objectName, TypeObject typeObject, String fullDirectory, String login) {
        if (objectRepository.findObjectDetailsByType(objectName, typeObject, fullDirectory, login) == null) {
            throw new NotFoundRequiredException("File with this name is not found");
        }
        return objectRepository.getObjectDetailsByObjectLocation(objectName, typeObject, fullDirectory, login);
    }
    @Transactional
    public List<ObjectInfoMiniDTO> getAllObjectsFromPath(String fullDirectory, String login) {
        return objectRepository.getObjectsNameByObjectLocation(fullDirectory, login);
    }

    public List<ObjectDetailsDTO> getObjectsByName_search(String objectName, String login) {
        return objectRepository.findObjectsByName(objectName, login);
    }
}
