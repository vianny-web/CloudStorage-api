package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.dto.ResponseMessage;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam MultipartFile object, @RequestParam String directory, Principal principal) {
        try {
            String objectName = directory + "/" + object.getOriginalFilename();

            InputStream inputStream = object.getInputStream();
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

    @DeleteMapping("/{directory}")
    public ResponseEntity<ResponseMessage> deleteFile(@PathVariable String directory, Principal principal) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(principal.getName())
                    .object(directory)
                    .build();
            minioConfig.minioClient().removeObject(removeObjectArgs);
        }
        catch (RuntimeException | ServerException | InsufficientDataException | ErrorResponseException | IOException |
               NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
               InternalException e) {
            throw new RuntimeException(e);
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.OK, "Файл успешно удален");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
