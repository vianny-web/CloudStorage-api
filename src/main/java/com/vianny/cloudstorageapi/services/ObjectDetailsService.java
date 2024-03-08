package com.vianny.cloudstorageapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObjectDetailsService {
    private final MinioService minioService;

    @Autowired
    public ObjectDetailsService(MinioService minioService) {
        this.minioService = minioService;
    }

    public void saveFolder(String folderName, String directory) {

    }
}
