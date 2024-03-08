package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.dto.ResponseMessage;
import com.vianny.cloudstorageapi.dto.request.RequestData;
import com.vianny.cloudstorageapi.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/myCloud")
public class MainController {
    private MinioService minioService;

    @Autowired
    public void setMinioService(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/createFolder")
    public ResponseEntity<ResponseMessage> createFolder(@RequestBody RequestData requestData, Principal principal) {
        minioService.createFolder(principal.getName(),requestData.getObjectName(),requestData.getObjectLocation());

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "Успешно");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }
}
