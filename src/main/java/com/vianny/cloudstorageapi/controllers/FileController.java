package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.dto.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.ObjectsInfoDTO;
import com.vianny.cloudstorageapi.dto.response.ResponseAllObjects;
import com.vianny.cloudstorageapi.dto.response.ResponseMessage;
import com.vianny.cloudstorageapi.dto.response.ResponseObjectDetails;
import com.vianny.cloudstorageapi.exception.requiredException.*;
import com.vianny.cloudstorageapi.services.AccountService;
import com.vianny.cloudstorageapi.services.FileService;
import com.vianny.cloudstorageapi.services.FileTransferService;
import com.vianny.cloudstorageapi.services.MinioService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/myCloud")
public class FileController {
    private MinioService minioService;
    private FileService fileService;
    private FileTransferService fileTransferService;
    private AccountService accountService;

    @Autowired
    public void setMinioService(MinioService minioService) {
        this.minioService = minioService;
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

    String fullDirectory;

    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<ResponseMessage> uploadFileToTheServer(@RequestParam MultipartFile file, @RequestParam String path, Principal principal) {
        try {
            fullDirectory = principal.getName() + "/" + path;

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

            HttpHeaders downloadHeaders = new HttpHeaders();
            downloadHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            downloadHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(path).build());

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
            fullDirectory = principal.getName() + "/" + path;

            List<ObjectDetailsDTO> objectDetails = fileService.getObject(filename, fullDirectory, principal.getName());
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
            fullDirectory = principal.getName() + "/" + path;

            List<ObjectsInfoDTO> objects = fileService.getObjectsName(fullDirectory, principal.getName());
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
            fullDirectory = principal.getName() + "/" + path;

            minioService.removeObject(fullDirectory, principal.getName());
            fileService.deleteFile(filename, fullDirectory, principal.getName());
            accountService.addSizeStorage(filename, principal.getName(), path);

        }
        catch (NotFoundRequiredException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK, "File successfully deleted");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
