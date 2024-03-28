package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.dto.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.ObjectsInfoDTO;
import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
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
    public List<ObjectDetailsDTO> getObject(String filename, String fullDirectory, String login) {
        if (objectRepository.findObjectDetailsByType(filename, TypeObject.File, fullDirectory, login) == null) {
            throw new NotFoundRequiredException("File with this name is not found");
        }
        return objectRepository.getObjectDetailsByObjectLocation(filename, TypeObject.File, fullDirectory, login);
    }
    @Transactional
    public List<ObjectsInfoDTO> getObjectsName(String fullDirectory, String login) {
        return objectRepository.getObjectsNameByObjectLocation(fullDirectory, login);
    }
}
