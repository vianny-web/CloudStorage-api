package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.models.Account;
import com.vianny.cloudstorageapi.models.ObjectDetails;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
import com.vianny.cloudstorageapi.repositories.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Service
public class ObjectService {
    private ObjectRepository objectRepository;

    @Autowired
    public void setObjectRepository(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    public void saveObject(MultipartFile object, String directory, Account account) {
        ObjectDetails objectDetails = new ObjectDetails();
        LocalDateTime uploadDate = LocalDateTime.now();

        objectDetails.setObjectName(object.getOriginalFilename());
        objectDetails.setObjectSize((int) object.getSize());
        objectDetails.setObjectLocation(directory);
        objectDetails.setUploadDate(uploadDate);

        objectDetails.setAccount(account);
        objectRepository.save(objectDetails);
    }
}