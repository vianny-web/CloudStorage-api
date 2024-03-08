package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.dto.ResponseMessage;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.Principal;

@RestController
@RequestMapping("/myCloud")
public class MainController {
    private MinioConfig minioConfig;
    @Autowired
    public void setMinioConfig(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam MultipartFile file, @RequestParam String directory, Principal principal) {
        try {
            String objectName = directory + "/" + file.getOriginalFilename();

            InputStream inputStream = file.getInputStream();
            minioConfig.minioClient().putObject(PutObjectArgs.builder()
                    .bucket(principal.getName())
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "Файл успешно загружен");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }
}
