package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.dto.response.object.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.response.object.ObjectInfoMiniDTO;
import com.vianny.cloudstorageapi.dto.response.object.ResponseAllObjects;
import com.vianny.cloudstorageapi.dto.response.object.ResponseObjectInfo;
import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.ServerErrorRequiredException;
import com.vianny.cloudstorageapi.services.ObjectService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/myCloud")
public class ObjectController {
    private ObjectService objectService;
    @Autowired
    public void setObjectService(ObjectService objectService) {
        this.objectService = objectService;
    }

    String fullDirectory;

    @GetMapping("/propertiesFile")
    public ResponseEntity<ResponseObjectInfo<List<ObjectDetailsDTO>>> getPropertiesFile(@RequestParam("path") String path, @RequestParam("type") TypeObject typeObject, @RequestParam("objectName") @NotBlank String objectName, Principal principal) {
        try {
            fullDirectory = principal.getName() + "/" + path;

            List<ObjectDetailsDTO> objectDetails = objectService.getObjectFromPath(objectName, typeObject, fullDirectory, principal.getName());
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
}