package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.dto.ResponseMessage;
import com.vianny.cloudstorageapi.exception.requiredException.ConflictRequiredException;
import com.vianny.cloudstorageapi.models.Account;
import com.vianny.cloudstorageapi.models.ObjectDetails;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/myCloud")
public class MainController {
    private MinioConfig minioConfig;
    private ObjectService objectService;
    @Autowired
    public void setMinioConfig(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }
    @Autowired
    public void setObjectService(ObjectService objectService) {
        this.objectService = objectService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam MultipartFile object, @RequestParam String directory, Principal principal) {
        try {
            String fullDirectory = directory + object.getOriginalFilename();
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

    @DeleteMapping("/")
    public ResponseEntity<ResponseMessage> deleteFile(@RequestParam("path") String path, Principal principal) {
        try {
            objectService.deleteObject(path, principal.getName());
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
