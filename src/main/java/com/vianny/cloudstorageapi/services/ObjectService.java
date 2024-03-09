package com.vianny.cloudstorageapi.services;

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
public class ObjectService {
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
    public void saveObject(MultipartFile object, String directory, String login) {
        ObjectDetails objectDetails = new ObjectDetails();
        Optional<Account> currentAccount = accountRepository.findUserByLogin(login);

        if (objectRepository.findByObjectLocationAndAccount_Login(directory, login) != null) {
            throw new ConflictRequiredException("Файл с таким именем в этом каталоге уже существует");
        }

        objectDetails.setObjectName(object.getOriginalFilename());
        objectDetails.setObjectSize((int) object.getSize());
        objectDetails.setObjectLocation(directory);
        objectDetails.setUploadDate(LocalDateTime.now());

        objectDetails.setAccount(currentAccount.orElseThrow());
        objectRepository.save(objectDetails);
    }

    @Transactional
    public void deleteObject(String directory, String login) {
        if (objectRepository.findByObjectLocationAndAccount_Login(directory, login) == null) {
            throw new NotFoundRequiredException("Файл с таким именем не найден");
        }
        else {
            objectRepository.deleteObjectDetailsByObjectLocation(directory);
        }
    }
}