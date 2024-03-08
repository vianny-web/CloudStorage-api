package com.vianny.cloudstorageapi.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${config.minio.endpoint}")
    private String minio_endpoint;

    @Value("${config.minio.login}")
    private String minio_login;

    @Value("${config.minio.password}")
    private String minio_password;

    private MinioClient minioClient;
    @Bean
    public MinioClient minioClient() {
        if (minioClient == null) {
            minioClient = MinioClient.builder()
                    .endpoint(minio_endpoint)
                    .credentials(minio_login, minio_password)
                    .build();
        }
        return minioClient;
    }
}
