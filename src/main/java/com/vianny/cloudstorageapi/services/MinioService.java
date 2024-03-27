package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
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

    public void removeObject(String filename, String path, String login) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(login)
                .object(path + filename)
                .build();

        minioConfig.minioClient().removeObject(removeObjectArgs);
    }
}
