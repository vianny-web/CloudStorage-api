package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.dto.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.ObjectsInfoDTO;
import com.vianny.cloudstorageapi.dto.response.ResponseAllObjects;
import com.vianny.cloudstorageapi.dto.response.ResponseMessage;
import com.vianny.cloudstorageapi.dto.response.ResponseObjectDetails;
import com.vianny.cloudstorageapi.exception.requiredException.*;
import com.vianny.cloudstorageapi.services.AccountService;
import com.vianny.cloudstorageapi.services.FileService;
import com.vianny.cloudstorageapi.services.FileTransferService;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/myCloud")
public class FIleController {
    private MinioConfig minioConfig;
    private FileService fileService;
    private FileTransferService fileTransferService;
    private AccountService accountService;
    @Autowired
    public void setMinioConfig(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }
    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }
    @Autowired
    public void setFileTransferService(FileTransferService fileTransferService) {
        this.fileTransferService = fileTransferService;
    }
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<ResponseMessage> uploadFileToTheServer(@RequestParam MultipartFile file, @RequestParam String path, Principal principal) {
        try {
            String fullDirectory = principal.getName() + "/" + path;

            fileTransferService.uploadFile(file, fullDirectory, principal.getName());
            accountService.reduceSizeStorage(principal.getName(), (int) file.getSize());
            fileService.saveFile(file, fullDirectory, principal.getName());
        }
        catch (NoContentRequiredException | NoStorageSpaceRequiredException | ConflictRequiredException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "File successfully uploaded");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @GetMapping("/download/")
    public ResponseEntity<Resource> downloadFileFromTheServer(@RequestParam String path, Principal principal) {
        try {
            InputStreamResource resource = fileTransferService.downloadFile(principal.getName(), path);
            System.out.println(resource.isFile());

            HttpHeaders downloadHeaders = new HttpHeaders();
            downloadHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            downloadHeaders.setContentDispositionFormData("attachment", path);

            return ResponseEntity.ok()
                    .headers(downloadHeaders)
                    .body(resource);
        }
        catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }
    }

    @GetMapping("/propertiesFile")
    public ResponseEntity<ResponseObjectDetails<List<ObjectDetailsDTO>>> getPropertiesFile(@RequestParam("path") String path, @RequestParam("filename") String filename, Principal principal) {
        try {
            List<ObjectDetailsDTO> objectDetails = fileService.getObject(filename, path, principal.getName());
            ResponseObjectDetails<List<ObjectDetailsDTO>> dataObject = new ResponseObjectDetails<>(HttpStatus.FOUND, objectDetails);
            return new ResponseEntity<>(dataObject, HttpStatus.OK);

        }
        catch (NotFoundRequiredException e) {
            throw e;
        }
        catch (Exception e) {
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
    @Transactional
    public ResponseEntity<ResponseMessage> deleteFile(@RequestParam("path") String path, @RequestParam("filename") String filename, Principal principal) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(principal.getName())
                    .object(path)
                    .build();
            minioConfig.minioClient().removeObject(removeObjectArgs);

            accountService.addSizeStorage(filename, principal.getName(), path);
            fileService.deleteFile(filename, path, principal.getName());
        }
        catch (NotFoundRequiredException e) {
            throw e;
        }
        catch (RuntimeException | ServerException | InsufficientDataException | ErrorResponseException | IOException |
               NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
               InternalException e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK, "File successfully deleted");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
