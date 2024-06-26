package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.exception.requiredException.ConflictRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.models.Account;
import com.vianny.cloudstorageapi.models.ObjectDetails;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
import com.vianny.cloudstorageapi.repositories.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FileService {
    private ObjectRepository objectRepository;
    private AccountRepository accountRepository;
    @Autowired
    public void setObjectRepository(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }
    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void saveFile(MultipartFile file, String fullDirectory, String login) {
        ObjectDetails objectDetails = new ObjectDetails();
        Optional<Account> currentAccount = accountRepository.findUserByLogin(login);

        if (objectRepository.findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(file.getOriginalFilename(), TypeObject.File, fullDirectory, login) != null) {
            throw new ConflictRequiredException("A file with this name already exists in this directory");
        }

        objectDetails.setObjectName(file.getOriginalFilename());
        objectDetails.setObjectType(TypeObject.File);
        objectDetails.setObjectSize((int) file.getSize());
        objectDetails.setObjectLocation(fullDirectory);
        objectDetails.setUploadDate(LocalDateTime.now());
        objectDetails.setAccount(currentAccount.orElseThrow());

        objectRepository.save(objectDetails);
    }
    @Transactional
    public void deleteFile(String filename, String path, String login) {
        if (objectRepository.findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(filename, TypeObject.File, path, login) == null) {
            throw new NotFoundRequiredException("File with this name is not found");
        }
        objectRepository.deleteByObjectLocation(filename, TypeObject.File, path, login);
    }
}