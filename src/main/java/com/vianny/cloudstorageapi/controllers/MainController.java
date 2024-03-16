package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.dto.AccountDTO;
import com.vianny.cloudstorageapi.dto.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.ObjectsInfoDTO;
import com.vianny.cloudstorageapi.dto.request.RequestFolder;
import com.vianny.cloudstorageapi.dto.response.ResponseAccountDetails;
import com.vianny.cloudstorageapi.dto.response.ResponseAllObjects;
import com.vianny.cloudstorageapi.dto.response.ResponseMessage;
import com.vianny.cloudstorageapi.dto.response.ResponseObjectDetails;
import com.vianny.cloudstorageapi.exception.requiredException.NoContentRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.ServerErrorRequiredException;
import com.vianny.cloudstorageapi.services.AccountService;
import com.vianny.cloudstorageapi.services.FileService;
import com.vianny.cloudstorageapi.services.FolderService;
import io.minio.*;
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

import java.io.ByteArrayInputStream;
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
    private FolderService folderService;
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
    @Autowired
    public void setFolderService(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/upload")
    @Transactional
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

            accountService.reduceSizeStorage(principal.getName(), (int) file.getSize());
            fileService.saveObject(file, fullDirectory, principal.getName());
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "File successfully uploaded");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @GetMapping("/download/")
    public ResponseEntity<Resource> downloadFile(@RequestParam String path, Principal principal) {
        try {
            InputStream fileStream = minioConfig.minioClient().getObject(
                    GetObjectArgs.builder()
                            .bucket(principal.getName())
                            .object(path)
                            .build()
            );
            InputStreamResource resource = new InputStreamResource(fileStream);

            HttpHeaders downloadHeaders = new HttpHeaders();
            downloadHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            downloadHeaders.setContentDispositionFormData("attachment", path);

            return ResponseEntity.ok()
                    .headers(downloadHeaders)
                    .body(resource);
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new NotFoundRequiredException(e.getMessage());
        }
    }

    @PostMapping("/createFolder")
    public ResponseEntity<ResponseMessage> createFolder(@RequestBody RequestFolder requestFolder, Principal principal) {
        try {
            folderService.saveFolder(requestFolder.getFolderName(), requestFolder.getPath(), principal.getName());
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "Folder successfully created");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteFolder/")
    public ResponseEntity<ResponseMessage> deleteFolder(@RequestParam("path") String path, @RequestParam("folderName") String folderName, Principal principal) {
        try {
            folderService.deleteFolder(folderName, path, principal.getName());
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "Folder successfully delete");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @GetMapping("/account/details")
    public ResponseEntity<ResponseAccountDetails<List<AccountDTO>>> getPropertiesFile(Principal principal) {
        try {
            List<AccountDTO> accountDetails = accountService.getAccountDetails(principal.getName());
            ResponseAccountDetails<List<AccountDTO>> dataObject = new ResponseAccountDetails<>(HttpStatus.FOUND, accountDetails);
            return new ResponseEntity<>(dataObject,HttpStatus.OK);
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }
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
    @Transactional
    public ResponseEntity<ResponseMessage> deleteFile(@RequestParam("path") String path, @RequestParam("filename") String filename, Principal principal) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(principal.getName())
                    .object(path)
                    .build();
            minioConfig.minioClient().removeObject(removeObjectArgs);

            accountService.addSizeStorage(filename, principal.getName(), path);
            fileService.deleteObject(filename, path, principal.getName());
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
