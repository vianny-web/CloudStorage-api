package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.dto.request.RequestFolder;
import com.vianny.cloudstorageapi.dto.response.message.ResponseMainMessage;
import com.vianny.cloudstorageapi.exception.requiredException.ConflictRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.ServerErrorRequiredException;
import com.vianny.cloudstorageapi.services.FolderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    private String fullDirectory;

    @PostMapping("/createFolder")
    public ResponseEntity<ResponseMainMessage> createFolder(@Valid @RequestBody RequestFolder requestFolder, Principal principal) {
        try {
            fullDirectory = principal.getName() + "/" + requestFolder.getPath();
            folderService.saveFolder(requestFolder.getFolderName(), fullDirectory, principal.getName());
        }
        catch (ConflictRequiredException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMainMessage responseMainMessage = new ResponseMainMessage(HttpStatus.CREATED, "Folder successfully created");
        return new ResponseEntity<>(responseMainMessage, HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteFolder/")
    public ResponseEntity<ResponseMainMessage> deleteFolder(@RequestParam("path") String path, @RequestParam("folderName") @NotBlank String folderName, Principal principal) {
        try {
            fullDirectory = principal.getName() + "/" + path;
            folderService.deleteFolder(folderName, fullDirectory, principal.getName());
        }
        catch (NotFoundRequiredException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMainMessage responseMainMessage = new ResponseMainMessage(HttpStatus.OK, "Folder successfully delete");
        return new ResponseEntity<>(responseMainMessage, HttpStatus.OK);
    }
}
