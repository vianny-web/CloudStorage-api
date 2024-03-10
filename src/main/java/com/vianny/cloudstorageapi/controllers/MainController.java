package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.dto.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.response.ResponseAllObjects;
import com.vianny.cloudstorageapi.dto.response.ResponseMessage;
import com.vianny.cloudstorageapi.dto.response.ResponseObjectDetails;
import com.vianny.cloudstorageapi.services.AccountService;
import com.vianny.cloudstorageapi.services.ObjectService;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/myCloud")
public class MainController {
    private MinioConfig minioConfig;
    private ObjectService objectService;
    private AccountService accountService;
    @Autowired
    public void setMinioConfig(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }
    @Autowired
    public void setObjectService(ObjectService objectService) {
        this.objectService = objectService;
    }
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam MultipartFile object, @RequestParam String path, Principal principal) {
        try {
            String fullDirectory = principal.getName() + "/" + path;
            objectService.saveObject(object, fullDirectory, principal.getName());

            InputStream inputStream = object.getInputStream();
            minioConfig.minioClient().putObject(PutObjectArgs.builder()
                    .bucket(principal.getName())
                    .object(fullDirectory)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", e);
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "Файл успешно загружен");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @GetMapping("/propertiesFile")
    public ResponseEntity<ResponseObjectDetails<List<ObjectDetailsDTO>>> getPropertiesFile(@RequestParam("path") String path, Principal principal) {
        try {
            List<ObjectDetailsDTO> objectDetails = objectService.getObject(path, principal.getName());
            ResponseObjectDetails<List<ObjectDetailsDTO>> dataObject = new ResponseObjectDetails<>(HttpStatus.FOUND, objectDetails);
            return new ResponseEntity<>(dataObject, HttpStatus.OK);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", e);
        }
    }

    @GetMapping("/")
    public ResponseEntity<ResponseAllObjects<List<String>>> getFiles(@RequestParam("path") String path, Principal principal) {
        try {
            List<String> objects = objectService.getObjectsName(path,principal.getName());
            ResponseAllObjects<List<String>> responseAllObjects = new ResponseAllObjects<>(HttpStatus.FOUND, objects);

            return new ResponseEntity<>(responseAllObjects,HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", e);
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<ResponseMessage> deleteFile(@RequestParam("path") String path, @RequestParam("filename") String filename, Principal principal) {
        try {
            objectService.deleteObject(filename, path, principal.getName());

            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(principal.getName())
                    .object(path)
                    .build();
            minioConfig.minioClient().removeObject(removeObjectArgs);

            ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK, "Файл успешно удален");
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        catch (RuntimeException | ServerException | InsufficientDataException | ErrorResponseException | IOException |
               NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
               InternalException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", e);
        }
    }
}
