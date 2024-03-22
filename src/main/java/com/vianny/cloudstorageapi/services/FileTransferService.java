package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.exception.requiredException.NoContentRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FileTransferService {
    private MinioConfig minioConfig;
    @Autowired
    public void setMinioConfig(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    public void uploadFile(MultipartFile file, String fullDirectory, String login) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (file.isEmpty()) {
            throw new NoContentRequiredException("No file content");
        }

        InputStream inputStream = file.getInputStream();
        minioConfig.minioClient().putObject(PutObjectArgs.builder()
                .bucket(login)
                .object(fullDirectory + "/" + file.getOriginalFilename())
                .stream(inputStream, file.getSize(), -1)
                .build());
    }

    public InputStreamResource downloadFile(String login, String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InputStream fileStream = minioConfig.minioClient().getObject(
                GetObjectArgs.builder()
                        .bucket(login)
                        .object( login + "/" + path)
                        .build()
        );
        return new InputStreamResource(fileStream);
    }
}
