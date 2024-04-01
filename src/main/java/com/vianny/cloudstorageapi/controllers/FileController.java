package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.dto.response.object.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.response.object.ObjectInfoMiniDTO;
import com.vianny.cloudstorageapi.dto.response.object.ResponseAllObjects;
import com.vianny.cloudstorageapi.dto.response.message.ResponseMainMessage;
import com.vianny.cloudstorageapi.dto.response.object.ResponseObjectInfo;
import com.vianny.cloudstorageapi.exception.requiredException.*;
import com.vianny.cloudstorageapi.services.*;
import io.minio.errors.*;
import jakarta.validation.constraints.NotBlank;
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
    private ObjectService objectService;
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
    public void setObjectService(ObjectService objectService) {
        this.objectService = objectService;
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
    public ResponseEntity<ResponseMainMessage> uploadFileToTheServer(@RequestParam MultipartFile file, @RequestParam String path, Principal principal) {
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

        ResponseMainMessage responseMainMessage = new ResponseMainMessage(HttpStatus.CREATED, "File successfully uploaded");
        return new ResponseEntity<>(responseMainMessage, HttpStatus.CREATED);
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
    public ResponseEntity<ResponseObjectInfo<List<ObjectDetailsDTO>>> getPropertiesFile(@RequestParam("path") String path, @RequestParam("filename") @NotBlank String filename, Principal principal) {
        try {
            fullDirectory = principal.getName() + "/" + path;

            List<ObjectDetailsDTO> objectDetails = objectService.getObjectFromPath(filename, fullDirectory, principal.getName());
            ResponseObjectInfo<List<ObjectDetailsDTO>> dataObject = new ResponseObjectInfo<>(HttpStatus.FOUND, objectDetails);
            return new ResponseEntity<>(dataObject, HttpStatus.FOUND);

        }
        catch (NotFoundRequiredException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<ResponseAllObjects<List<ObjectInfoMiniDTO>>> getFilesFromPath(@RequestParam("path") String path, Principal principal) {
        try {
            fullDirectory = principal.getName() + "/" + path;

            List<ObjectInfoMiniDTO> objects = objectService.getAllObjectsFromPath(fullDirectory, principal.getName());
            ResponseAllObjects<List<ObjectInfoMiniDTO>> responseAllObjects = new ResponseAllObjects<>(HttpStatus.FOUND, objects);

            return new ResponseEntity<>(responseAllObjects,HttpStatus.FOUND);
        } catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseAllObjects<List<ObjectDetailsDTO>>> getFilesByName(@RequestParam("objectName") @NotBlank String objectName, Principal principal) {
        try {
            List<ObjectDetailsDTO> objects = objectService.getObjectsByName_search(objectName, principal.getName());
            ResponseAllObjects<List<ObjectDetailsDTO>> responseAllObjects = new ResponseAllObjects<>(HttpStatus.FOUND, objects);

            return new ResponseEntity<>(responseAllObjects,HttpStatus.FOUND);
        } catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }
    }

    @DeleteMapping("/")
    @Transactional
    public ResponseEntity<ResponseMainMessage> deleteFile(@RequestParam("path") String path, @RequestParam("filename") @NotBlank String filename, Principal principal) {
        try {
            fullDirectory = principal.getName() + "/" + path;

            minioService.removeObject(fullDirectory, principal.getName());
            accountService.addSizeStorage(filename, principal.getName(), fullDirectory);
            fileService.deleteFile(filename, fullDirectory, principal.getName());

        }
        catch (NotFoundRequiredException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMainMessage responseMainMessage = new ResponseMainMessage(HttpStatus.OK, "File successfully deleted");
        return new ResponseEntity<>(responseMainMessage, HttpStatus.OK);
    }
}
