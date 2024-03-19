package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.dto.request.RequestFolder;
import com.vianny.cloudstorageapi.dto.response.ResponseMessage;
import com.vianny.cloudstorageapi.exception.requiredException.ServerErrorRequiredException;
import com.vianny.cloudstorageapi.services.AccountService;
import com.vianny.cloudstorageapi.services.FileService;
import com.vianny.cloudstorageapi.services.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/myCloud")
public class FolderController {
    private FolderService folderService;
    @Autowired
    public void setFolderService(FolderService folderService) {
        this.folderService = folderService;
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
}
