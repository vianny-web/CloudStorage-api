package com.vianny.cloudstorageapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vianny.cloudstorageapi.exception.handlers.CustomExceptionHandler;
import com.vianny.cloudstorageapi.exception.requiredException.ConflictRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NoContentRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NoStorageSpaceRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.services.AccountService;
import com.vianny.cloudstorageapi.services.FileService;
import com.vianny.cloudstorageapi.services.FileTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.security.Principal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {
    @Mock
    private FileService fileService;
    @Mock
    private FileTransferService fileTransferService;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private FileController fileController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    MockMultipartFile file;
    String path;
    Principal principal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();


        file = new MockMultipartFile("file", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "test file".getBytes());
        path = "files/";
        principal = () -> "user";
    }

    // Тестирование всех случаев метода "uploadFileToTheServer"
    @Test
    void testUploadFileToTheServer() throws Exception {
        mockMvc.perform(multipart("/myCloud/upload")
                        .file(file)
                        .param("path", path)
                        .principal(principal))
                .andExpect(status().isCreated());

        verify(fileTransferService, times(1)).uploadFile(file, principal.getName() + "/" + path, principal.getName());
        verify(accountService, times(1)).reduceSizeStorage(principal.getName(), (int) file.getSize());
        verify(fileService, times(1)).saveFile(file, principal.getName() + "/" + path, principal.getName());
    }
    @Test
    void testUploadFileToTheServer_NoContentRequiredException() throws Exception {
        doThrow(NoContentRequiredException.class).when(fileTransferService).uploadFile(any(), anyString(), anyString());

        mockMvc.perform(multipart("/myCloud/upload")
                        .file(file)
                        .param("path", path)
                        .principal(principal))
                .andExpect(status().isNoContent());
    }
    @Test
    void testUploadFileToTheServer_NoStorageSpaceRequiredException() throws Exception {
        doThrow(NoStorageSpaceRequiredException.class).when(accountService).reduceSizeStorage(anyString(), anyInt());

        mockMvc.perform(multipart("/myCloud/upload")
                        .file(file)
                        .param("path", path)
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testUploadFileToTheServer_ConflictRequiredException() throws Exception {
        doThrow(ConflictRequiredException.class).when(fileService).saveFile(any(), anyString(), anyString());

        mockMvc.perform(multipart("/myCloud/upload")
                        .file(file)
                        .param("path", path)
                        .principal(principal))
                .andExpect(status().isConflict());
    }


    // Тестирование всех случаев метода "downloadFileFromTheServer"
    @Test
    void testDownloadFileFromTheServer() throws Exception {
        InputStreamResource mockResource = new InputStreamResource(new ByteArrayInputStream(new byte[0]));
        when(fileTransferService.downloadFile(anyString(), anyString())).thenReturn(mockResource);

        mockMvc.perform(get("/myCloud/download/")
                        .param("path", path)
                        .principal(() -> principal.getName()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path + "\""));
    }
    @Test
    void testDownloadFileFromTheServer_ServerErrorRequiredException() throws Exception {
        InputStreamResource mockResource = new InputStreamResource(new ByteArrayInputStream(new byte[0]));

        when(fileTransferService.downloadFile(anyString(), anyString())).thenReturn(mockResource);

        mockMvc.perform(get("/myCloud/download/")
                        .param("path", path)
                        .principal(() -> principal.getName()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path + "\""));
    }
    @Test
    void testDownloadFileFromTheServer_NotFoundRequiredException() throws Exception {
        doThrow(NotFoundRequiredException.class).when(fileTransferService).downloadFile(anyString(), anyString());

        mockMvc.perform(get("/myCloud/download/")
                        .param("path", path)
                        .principal(() -> principal.getName()))
                .andExpect(status().isNotFound());
    }


    // Тестирование всех случаев метода "getPropertiesFile"
    @Test
    void testGetPropertiesFile() {

    }
}
