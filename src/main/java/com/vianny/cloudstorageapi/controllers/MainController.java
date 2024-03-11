package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.dto.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.ObjectsInfoDTO;
import com.vianny.cloudstorageapi.dto.response.ResponseAllObjects;
import com.vianny.cloudstorageapi.dto.response.ResponseMessage;
import com.vianny.cloudstorageapi.dto.response.ResponseObjectDetails;
import com.vianny.cloudstorageapi.exception.requiredException.NoAccessRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NoContentRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.ServerErrorRequiredException;
import com.vianny.cloudstorageapi.services.AccountService;
import com.vianny.cloudstorageapi.services.FileService;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;

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
    private FileService fileService;
    private AccountService accountService;
    @Autowired
    public void setMinioConfig(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }
    @Autowired
    public void setObjectService(FileService fileService) {
        this.fileService = fileService;
    }
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam MultipartFile file, @RequestParam String path, Principal principal) {
        try {
            if (file.isEmpty()) {
                throw new NoContentRequiredException("No file content");
            }
            String fullDirectory = principal.getName() + "/" + path;

            InputStream inputStream = file.getInputStream();
            minioConfig.minioClient().putObject(PutObjectArgs.builder()
                    .bucket(principal.getName())
                    .object(fullDirectory + "/" + file.getOriginalFilename())
                    .stream(inputStream, file.getSize(), -1)
                    .build());
            fileService.saveObject(file, fullDirectory, principal.getName());
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "File successfully uploaded");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }


    @GetMapping("/propertiesFile")
    public ResponseEntity<ResponseObjectDetails<List<ObjectDetailsDTO>>> getPropertiesFile(@RequestParam("path") String path, @RequestParam("filename") String filename, Principal principal) {
        try {
            List<ObjectDetailsDTO> objectDetails = fileService.getObject(filename, path, principal.getName());
            ResponseObjectDetails<List<ObjectDetailsDTO>> dataObject = new ResponseObjectDetails<>(HttpStatus.FOUND, objectDetails);
            return new ResponseEntity<>(dataObject, HttpStatus.OK);

        } catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<ResponseAllObjects<List<ObjectsInfoDTO>>> getFiles(@RequestParam("path") String path, Principal principal) {
        try {
            List<ObjectsInfoDTO> objects = fileService.getObjectsName(path, principal.getName());
            ResponseAllObjects<List<ObjectsInfoDTO>> responseAllObjects = new ResponseAllObjects<>(HttpStatus.FOUND, objects);

            return new ResponseEntity<>(responseAllObjects,HttpStatus.OK);
        } catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<ResponseMessage> deleteFile(@RequestParam("path") String path, @RequestParam("filename") String filename, Principal principal) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(principal.getName())
                    .object(path)
                    .build();
            minioConfig.minioClient().removeObject(removeObjectArgs);

            fileService.deleteObject(filename, path, principal.getName());

            ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK, "File successfully deleted");
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        catch (RuntimeException | ServerException | InsufficientDataException | ErrorResponseException | IOException |
               NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
               InternalException e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }
    }
}
