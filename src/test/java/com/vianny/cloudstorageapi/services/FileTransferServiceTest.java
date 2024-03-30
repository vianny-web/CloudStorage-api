package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.exception.requiredException.NoContentRequiredException;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.security.Principal;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileTransferServiceTest {
    @Mock
    private MinioConfig minioConfigMock;

    @Mock
    private MinioClient minioClientMock;

    @InjectMocks
    private FileTransferService fileTransferService;

    Principal principal;
    String fullDirectory;
    String filename;
    String path;

    @BeforeEach
    void setUp() {
        principal = () -> "user-1";
        fullDirectory = principal.getName() + "/" + "files/";
        path = "files/";
        filename = "file.txt";
    }

    // Тестирование всех случаев метода "uploadFile"
    @Test
    public void testUploadFile() throws Exception {
        byte[] fileContent = "test text".getBytes();

        MockMultipartFile multipartFile = new MockMultipartFile("file", filename, "", fileContent);

        when(minioConfigMock.minioClient()).thenReturn(minioClientMock);
        when(minioClientMock.putObject(any())).thenReturn(null);

        fileTransferService.uploadFile(multipartFile, fullDirectory, principal.getName());
    }
    @Test
    public void testUploadFile_NoContentRequiredException() {
        MockMultipartFile emptyFile = new MockMultipartFile("emptyFile", new byte[0]);

        assertThrows(NoContentRequiredException.class, () -> fileTransferService.uploadFile(emptyFile, fullDirectory, principal.getName()));
    }
}
