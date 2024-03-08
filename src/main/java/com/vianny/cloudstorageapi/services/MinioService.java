package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.config.MinioConfig;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinioService {
    private MinioConfig minioConfig;

    private final ObjectDetailsService objectDetailsService;
    @Autowired
    public MinioService(ObjectDetailsService objectDetailsService) {
        this.objectDetailsService = objectDetailsService;
    }

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
    public void createFolder(String username, String folderName, String directory) {

    }


}
