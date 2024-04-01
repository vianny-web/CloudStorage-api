package com.vianny.cloudstorageapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vianny.cloudstorageapi.dto.response.object.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.response.object.ObjectInfoMiniDTO;
import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.exception.handlers.CustomExceptionHandler;
import com.vianny.cloudstorageapi.exception.requiredException.ConflictRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NoContentRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NoStorageSpaceRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {
    @Mock
    private MinioService minioService;
    @Mock
    private FileService fileService;
    @Mock
    private ObjectService objectService;
    @Mock
    private FileTransferService fileTransferService;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private FileController fileController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    MockMultipartFile file;
    String path, filename, foldername, full_directory;
    int size;
    LocalDateTime time;
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

        filename = "file.txt";
        foldername = "photos";
        full_directory = "user/files/";
        size = 1000;
        time = LocalDateTime.now();
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
    void testGetPropertiesFile() throws Exception {
        List<ObjectDetailsDTO> objectDetailsList = new ArrayList<>();
        ObjectDetailsDTO objectDetails = new ObjectDetailsDTO(filename, TypeObject.File, full_directory, size, time);
        objectDetailsList.add(objectDetails);

        when(objectService.getObjectFromPath(eq(filename), eq(full_directory), eq(principal.getName()))).thenReturn(objectDetailsList);

        mockMvc.perform(get("/myCloud/propertiesFile")
                        .param("path", path)
                        .param("filename", filename)
                        .principal(principal))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.httpStatus").value("FOUND"))
                .andExpect(jsonPath("$.properties[0].objectName").value(filename))
                .andExpect(jsonPath("$.properties[0].objectType").value(TypeObject.File.toString()))
                .andExpect(jsonPath("$.properties[0].objectLocation").value(full_directory))
                .andExpect(jsonPath("$.properties[0].objectSize").value(size));

        verify(objectService, times(1)).getObjectFromPath(eq(filename), eq(full_directory), eq(principal.getName()));
    }
    @Test
    void testGetPropertiesFile_NotFoundRequiredException() throws Exception {
        doThrow(NotFoundRequiredException.class).when(objectService).getObjectFromPath(filename, full_directory, principal.getName());

        mockMvc.perform(get("/myCloud/propertiesFile")
                        .param("path", path)
                        .param("filename", filename)
                        .principal(() -> principal.getName()))
                .andExpect(status().isNotFound());
    }


    // Тестирование всех случаев метода "getFiles"
    @Test
    void testGetFiles() throws Exception {
        List<ObjectInfoMiniDTO> objectInfoMiniDTOS = new ArrayList<>();
        ObjectInfoMiniDTO objectInfo1 = new ObjectInfoMiniDTO(filename, TypeObject.File);
        ObjectInfoMiniDTO objectInfo2 = new ObjectInfoMiniDTO(foldername, TypeObject.Folder);
        objectInfoMiniDTOS.add(objectInfo1);
        objectInfoMiniDTOS.add(objectInfo2);

        when(objectService.getAllObjectsFromPath(eq(full_directory), eq(principal.getName()))).thenReturn(objectInfoMiniDTOS);

        mockMvc.perform(get("/myCloud/")
                        .param("path", path)
                        .principal(principal))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.httpStatus").value("FOUND"))
                .andExpect(jsonPath("$.objects[0].objectName").value(filename))
                .andExpect(jsonPath("$.objects[0].objectType").value(TypeObject.File.toString()))
                .andExpect(jsonPath("$.objects[1].objectName").value(foldername))
                .andExpect(jsonPath("$.objects[1].objectType").value(TypeObject.Folder.toString()));

        verify(objectService, times(1)).getAllObjectsFromPath(eq(full_directory), eq(principal.getName()));
    }


    // Тестирование всех случаев метода "deleteFile"
    @Test
    void testDeleteFile() throws Exception {
        mockMvc.perform(delete("/myCloud/")
                        .principal(principal)
                        .param("path", path)
                        .param("filename", filename))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus", is("OK")));

        verify(minioService, times(1)).removeObject(full_directory, principal.getName());
        verify(accountService, times(1)).addSizeStorage(filename, principal.getName(), full_directory);
        verify(fileService, times(1)).deleteFile(filename, full_directory, principal.getName());
    }
    @Test
    void testDeleteFile_NotFoundRequiredException() throws Exception {
        doThrow(NotFoundRequiredException.class).when(fileService).deleteFile(filename, full_directory, principal.getName());

        mockMvc.perform(delete("/myCloud/")
                        .principal(principal)
                        .param("path", path)
                        .param("filename", filename))
                .andExpect(status().isNotFound());
    }

    // Тестирование всех случаев метода "getFilesByName"
    @Test
    void testGetFilesByName() throws Exception {
        List<ObjectDetailsDTO> objectDetailsDTOS = new ArrayList<>();
        ObjectDetailsDTO file1 = new ObjectDetailsDTO("file", TypeObject.File, "", 10, LocalDateTime.now());
        ObjectDetailsDTO file2 = new ObjectDetailsDTO("file", TypeObject.File, "files/", 10, LocalDateTime.now());
        ObjectDetailsDTO file3 = new ObjectDetailsDTO("file", TypeObject.Folder, "files/", 10, LocalDateTime.now());

        objectDetailsDTOS.add(file1);
        objectDetailsDTOS.add(file2);
        objectDetailsDTOS.add(file3);

        when(objectService.getObjectsByName_search("file", principal.getName())).thenReturn(objectDetailsDTOS);

        mockMvc.perform(get("/myCloud/search")
                        .principal(principal)
                        .param("objectName", "file"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.httpStatus", is("FOUND")))
                        .andExpect((jsonPath("$.objects[0].objectName", is("file"))))
                        .andExpect((jsonPath("$.objects[1].objectName", is("file"))))
                        .andExpect((jsonPath("$.objects[2].objectName", is("file"))));

        verify(objectService, times(1)).getObjectsByName_search("file", principal.getName());
    }
}
