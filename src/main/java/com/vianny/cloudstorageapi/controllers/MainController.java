package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.dto.ResponseMessage;
import com.vianny.cloudstorageapi.dto.request.RequestData;
import com.vianny.cloudstorageapi.models.ObjectDetails;
import com.vianny.cloudstorageapi.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/myCloud")
public class MainController {
    private final MinioService minioService;

    @Autowired
    public MainController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam MultipartFile file, @RequestParam String directory, Principal principal) {
        minioService.uploadFile(file, principal.getName(), directory);

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "Файл успешно загружен");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }
}
