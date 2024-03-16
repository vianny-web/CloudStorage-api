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

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FolderService {
    private ObjectRepository objectRepository;
    private AccountRepository accountRepository;
    @Autowired
    public void setFileRepository(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }
    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void saveFolder(String folderName, String path, String login) {
        if (objectRepository.findByObjectNameAndObjectLocationAndAccount_Login(folderName,login  + "/" + path, login) != null) {
            throw new ConflictRequiredException("A folder with this name already exists in this directory");
        }

        ObjectDetails objectDetails = new ObjectDetails();
        Optional<Account> currentAccount = accountRepository.findUserByLogin(login);

        objectDetails.setObjectName(folderName);
        objectDetails.setObjectType(TypeObject.Folder);
        objectDetails.setObjectSize(0);
        objectDetails.setObjectLocation(login + "/" + path);
        objectDetails.setUploadDate(LocalDateTime.now());
        objectDetails.setAccount(currentAccount.orElseThrow());

        objectRepository.save(objectDetails);
    }

    @Transactional
    public void deleteFolder(String filename, String path, String login) {
        if (objectRepository.findByObjectNameAndObjectLocationAndAccount_Login(filename, path, login) == null) {
            throw new NotFoundRequiredException("Folder with this name is not found");
        }
        objectRepository.deleteObjectDetailsByObjectLocation(filename, TypeObject.Folder, path, login);
    }
}
