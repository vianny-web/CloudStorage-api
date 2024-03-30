package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        if (!minioConfig.minioClient().bucketExists(BucketExistsArgs.builder().bucket(username).build())) {
            minioConfig.minioClient().makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(username)
                            .build()
            );
        }
    }

    public void removeObject(String fullDirectory, String login) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(login)
                .object(fullDirectory)
                .build();

        minioConfig.minioClient().removeObject(removeObjectArgs);
    }
}
