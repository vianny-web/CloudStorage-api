package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.repositories.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {
    private ObjectRepository objectRepository;
    @Autowired
    public void setFileRepository(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    private void saveFolder() {

    }
}
