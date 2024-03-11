package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.dto.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.ObjectsInfoDTO;
import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.exception.requiredException.ConflictRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.ServerErrorRequiredException;
import com.vianny.cloudstorageapi.models.Account;
import com.vianny.cloudstorageapi.models.ObjectDetails;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
import com.vianny.cloudstorageapi.repositories.FileRepository;
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
public class FileService {
    private FileRepository fileRepository;
    private AccountRepository accountRepository;
    @Autowired
    public void setObjectRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }
    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void saveObject(MultipartFile file, String path, String login) {
        ObjectDetails objectDetails = new ObjectDetails();
        Optional<Account> currentAccount = accountRepository.findUserByLogin(login);

        if (fileRepository.findByObjectNameAndObjectLocationAndAccount_Login(file.getOriginalFilename(),path,login) != null) {
            throw new ConflictRequiredException("A file with this name already exists in this directory");
        }

        objectDetails.setObjectName(file.getOriginalFilename());
        objectDetails.setObjectType(TypeObject.File);
        objectDetails.setObjectSize((int) file.getSize());
        objectDetails.setObjectLocation(path);
        objectDetails.setUploadDate(LocalDateTime.now());
        objectDetails.setAccount(currentAccount.orElseThrow());
        reduceSizeStorage(login, (int) file.getSize());

        fileRepository.save(objectDetails);
    }

    @Transactional
    public List<ObjectDetailsDTO> getObject(String filename, String path, String login) {
        if (fileRepository.findByObjectNameAndObjectLocationAndAccount_Login(filename, path, login) == null) {
            throw new NotFoundRequiredException("File with this name is not found");
        }
        return fileRepository.getObjectDetailsByObjectLocation(filename, path, login);
    }

    @Transactional
    public List<ObjectsInfoDTO> getObjectsName(String path, String login) {
        return fileRepository.getObjectsNameByObjectLocation(path,login);
    }

    @Transactional
    public void deleteObject(String filename, String path, String login) {
        if (fileRepository.findByObjectNameAndObjectLocationAndAccount_Login(filename, path, login) == null) {
            throw new NotFoundRequiredException("File with this name is not found");
        }
        addSizeStorage(filename, login, path);
        fileRepository.deleteObjectDetailsByObjectLocation(path, login);
    }


    @Transactional
    public void reduceSizeStorage(String login, int sizeObject) {
        accountRepository.subtractBytesFromSizeStorage(login,sizeObject);
    }
    @Transactional
    public void addSizeStorage(String filename, String login, String path) {
        int sizeObject = fileRepository.findByObjectNameAndObjectLocationAndAccount_Login(filename, path, login).getObjectSize();
        accountRepository.updateSizeStorageByLogin(login, sizeObject);
    }
}