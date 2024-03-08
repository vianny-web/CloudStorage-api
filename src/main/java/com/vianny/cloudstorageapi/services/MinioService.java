package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.config.MinioConfig;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioService {
    private MinioConfig minioConfig;

    @Autowired
    public void setMinioConfig(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    public void createBucket(String username) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioConfig.minioClient().makeBucket(
                MakeBucketArgs
                        .builder()
                        .bucket(username)
                        .build()
        );
    }

    public void uploadFile(MultipartFile file, String username, String directory) {
        try {
            String objectName = directory + "/" + file.getOriginalFilename();

            InputStream inputStream = file.getInputStream();
            minioConfig.minioClient().putObject(PutObjectArgs.builder()
                    .bucket(username)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
