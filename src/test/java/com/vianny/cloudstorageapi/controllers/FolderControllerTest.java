package com.vianny.cloudstorageapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vianny.cloudstorageapi.dto.request.RequestFolder;
import com.vianny.cloudstorageapi.exception.handlers.CustomExceptionHandler;
import com.vianny.cloudstorageapi.exception.requiredException.ConflictRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class FolderControllerTest {
    @Mock
    private FolderService folderService;

    @InjectMocks
    private FolderController folderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    String path, foldername, full_directory;
    Principal principal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(folderController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        path = "photos/";
        principal = () -> "user";

        foldername = "files";
        full_directory = "user/photos/";

    }

    // Тестирование всех случаев метода "createFolder"
    @Test
    void testCreateFolder() throws Exception {
        RequestFolder requestFolder = new RequestFolder(foldername, path);
        String folderJson = objectMapper.writeValueAsString(requestFolder);

        mockMvc.perform(post("/myCloud/createFolder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(folderJson)
                        .principal(principal))
                .andExpect(status().isCreated());

        verify(folderService, times(1)).saveFolder(foldername, full_directory, principal.getName());
    }

    @Test
    void testCreateFolder_ConflictRequiredException() throws Exception {
        RequestFolder requestFolder = new RequestFolder(foldername, path);
        String folderJson = objectMapper.writeValueAsString(requestFolder);

        doThrow(ConflictRequiredException.class).when(folderService).saveFolder(foldername, full_directory, principal.getName());

        mockMvc.perform(post("/myCloud/createFolder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(folderJson)
                        .principal(principal))
                .andExpect(status().isConflict());
    }


    // Тестирование всех случаев метода "deleteFolder"
    @Test
    void testDeleteFolder() throws Exception {
        mockMvc.perform(delete("/myCloud/deleteFolder/")
                        .principal(principal)
                        .param("path", path)
                        .param("folderName", foldername))
                .andExpect(status().isOk());

        verify(folderService, times(1)).deleteFolder(foldername, full_directory, principal.getName());
    }
    @Test
    void testDeleteFolder_NotFoundRequiredException() throws Exception {
        doThrow(NotFoundRequiredException.class).when(folderService).deleteFolder(foldername, full_directory, principal.getName());

        mockMvc.perform(delete("/myCloud/deleteFolder/")
                        .principal(principal)
                        .param("path", path)
                        .param("folderName", foldername))
                .andExpect(status().isNotFound());
    }

}
