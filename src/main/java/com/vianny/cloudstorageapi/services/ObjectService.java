package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.dto.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.exception.requiredException.ConflictRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.models.Account;
import com.vianny.cloudstorageapi.models.ObjectDetails;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
import com.vianny.cloudstorageapi.repositories.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
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
        reduceSizeStorage(login, (int) object.getSize());

        objectRepository.save(objectDetails);
    }

    @Transactional
    public List<ObjectDetailsDTO> getObject(String path, String login) {
        if (objectRepository.findByObjectLocationAndAccount_Login(path, login) == null) {
            throw new NotFoundRequiredException("Файл с таким именем не найден");
        }
        return objectRepository.getObjectDetailsByObjectLocation(path, login);
    }

    @Transactional
    public List<String> getObjectsName(String path, String login) {
        if (objectRepository.findByObjectLocationAndAccount_Login(path, login) == null) {
            throw new NotFoundRequiredException("В этой директории нет файлов");
        }
        return objectRepository.getObjectsNameByObjectLocation(path,login);
    }

    @Transactional
    public void deleteObject(String path, String login) {
        if (objectRepository.findByObjectLocationAndAccount_Login(path, login) == null) {
            throw new NotFoundRequiredException("Файл с таким именем не найден");
        }
        addSizeStorage(login, path);
        objectRepository.deleteObjectDetailsByObjectLocation(path, login);
    }


    @Transactional
    public void reduceSizeStorage(String login, int sizeObject) {
        try {
            accountRepository.subtractBytesFromSizeStorage(login,sizeObject);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", e);
        }
    }
    @Transactional
    public void addSizeStorage(String login, String path) {
        try {
            int sizeObject = objectRepository.findByObjectLocationAndAccount_Login(path, login).getObjectSize();
            accountRepository.updateSizeStorageByLogin(login, sizeObject);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", e);
        }
    }
}