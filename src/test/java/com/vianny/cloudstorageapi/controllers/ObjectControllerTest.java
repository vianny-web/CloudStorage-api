package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.dto.response.object.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.response.object.ObjectInfoMiniDTO;
import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.exception.handlers.CustomExceptionHandler;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.services.ObjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ObjectControllerTest {
    @Mock
    private ObjectService objectService;
    @InjectMocks
    private ObjectController objectController;

    private MockMvc mockMvc;

    MockMultipartFile file;
    String path, filename, foldername, full_directory;
    int size;
    LocalDateTime time;
    Principal principal;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(objectController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();

        file = new MockMultipartFile("file", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "test file".getBytes());
        path = "objects/";
        principal = () -> "user";

        filename = "file.txt";
        foldername = "photos";
        full_directory = "user/objects/";
        size = 1000;
        time = LocalDateTime.now();
    }


    // Тестирование всех случаев метода "getPropertiesObject"
    @Test
    void testGetPropertiesObject_file() throws Exception {
        List<ObjectDetailsDTO> objectDetailsList = new ArrayList<>();
        ObjectDetailsDTO objectDetails = new ObjectDetailsDTO(filename, TypeObject.File, full_directory, size, time);
        objectDetailsList.add(objectDetails);

        when(objectService.getObjectFromPath(eq(filename), eq(TypeObject.File), eq(full_directory), eq(principal.getName()))).thenReturn(objectDetailsList);

        mockMvc.perform(get("/myCloud/propertiesFile")
                        .param("path", path)
                        .param("objectName", filename)
                        .param("type", TypeObject.File.toString())
                        .principal(principal))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.httpStatus").value("FOUND"))
                .andExpect(jsonPath("$.properties[0].objectName").value(filename))
                .andExpect(jsonPath("$.properties[0].objectType").value(TypeObject.File.toString()))
                .andExpect(jsonPath("$.properties[0].objectLocation").value(full_directory))
                .andExpect(jsonPath("$.properties[0].objectSize").value(size));

        verify(objectService, times(1)).getObjectFromPath(eq(filename), eq(TypeObject.File), eq(full_directory), eq(principal.getName()));
    }

    @Test
    void testGetPropertiesObject_folder() throws Exception {
        List<ObjectDetailsDTO> objectDetailsList = new ArrayList<>();
        ObjectDetailsDTO objectDetails = new ObjectDetailsDTO(foldername, TypeObject.Folder, full_directory, 0, time);
        objectDetailsList.add(objectDetails);

        when(objectService.getObjectFromPath(eq(foldername), eq(TypeObject.Folder), eq(full_directory), eq(principal.getName()))).thenReturn(objectDetailsList);

        mockMvc.perform(get("/myCloud/propertiesFile")
                        .param("path", path)
                        .param("objectName", foldername)
                        .param("type", TypeObject.Folder.toString())
                        .principal(principal))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.httpStatus").value("FOUND"))
                .andExpect(jsonPath("$.properties[0].objectName").value(foldername))
                    .andExpect(jsonPath("$.properties[0].objectType").value(TypeObject.Folder.toString()))
                .andExpect(jsonPath("$.properties[0].objectLocation").value(full_directory))
                .andExpect(jsonPath("$.properties[0].objectSize").value(0));

        verify(objectService, times(1)).getObjectFromPath(eq(foldername), eq(TypeObject.Folder), eq(full_directory), eq(principal.getName()));
    }

    @Test
    void testGetPropertiesObject_file_NotFoundRequiredException() throws Exception {
        doThrow(NotFoundRequiredException.class).when(objectService).getObjectFromPath(filename, TypeObject.File, full_directory, principal.getName());

        mockMvc.perform(get("/myCloud/propertiesFile")
                        .param("path", path)
                        .param("objectName", filename)
                        .param("type", TypeObject.File.toString())
                        .principal(() -> principal.getName()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPropertiesObject_folder_NotFoundRequiredException() throws Exception {
        doThrow(NotFoundRequiredException.class).when(objectService).getObjectFromPath(foldername, TypeObject.Folder, full_directory, principal.getName());

        mockMvc.perform(get("/myCloud/propertiesFile")
                        .param("path", path)
                        .param("objectName", foldername)
                        .param("type", TypeObject.Folder.toString())
                        .principal(() -> principal.getName()))
                .andExpect(status().isNotFound());
    }


//    // Тестирование всех случаев метода "getFiles"
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


//    // Тестирование всех случаев метода "getFilesByName"
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
