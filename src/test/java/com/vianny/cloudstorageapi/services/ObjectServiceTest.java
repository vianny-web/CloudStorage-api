package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.dto.response.object.ObjectDetailsDTO;
import com.vianny.cloudstorageapi.dto.response.object.ObjectInfoMiniDTO;
import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.models.ObjectDetails;
import com.vianny.cloudstorageapi.repositories.ObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ObjectServiceTest {
    @Mock
    private ObjectRepository objectRepository;
    @InjectMocks
    private ObjectService objectService;

    Principal principal;
    String fullDirectory;

    @BeforeEach
    void setUp() {
        principal = () -> "user-1";

        fullDirectory = principal.getName() + "/" + "files/";
    }

    // Тестирование всех случаев метода "getObject"
    @Test
    void testGetObject() {
        List<ObjectDetailsDTO> objectDetailsDTOS = new ArrayList<>();
        ObjectDetailsDTO file1 = new ObjectDetailsDTO("file", TypeObject.File, fullDirectory, 10, LocalDateTime.now());
        objectDetailsDTOS.add(file1);

        ObjectDetails objectDetails = new ObjectDetails("file", TypeObject.File, 10,fullDirectory, LocalDateTime.now());

        Mockito.when(objectRepository.findObjectDetailsByType("file", TypeObject.File, fullDirectory, principal.getName()))
                .thenReturn(objectDetails);
        Mockito.when(objectRepository.getObjectDetailsByObjectLocation("file", TypeObject.File, fullDirectory, principal.getName())).thenReturn(objectDetailsDTOS);

        List<ObjectDetailsDTO> result = objectService.getObjectFromPath("file", fullDirectory, principal.getName());

        assertEquals(objectDetailsDTOS, result);
    }
    @Test
    void testGetObject_NotFoundRequiredException() {
        Mockito.when(objectRepository.findObjectDetailsByType("file", TypeObject.File, fullDirectory, principal.getName()))
                .thenReturn(null);

        assertThrows(NotFoundRequiredException.class,() -> objectService.getObjectFromPath("file", fullDirectory, principal.getName()));
    }


    // Тестирование всех случаев метода "getObjectsName"
    @Test
    void testGetObjectsName() {
        List<ObjectInfoMiniDTO> objectInfoMiniDTOS = new ArrayList<>();
        ObjectInfoMiniDTO file1 = new ObjectInfoMiniDTO("file-1", TypeObject.File);
        ObjectInfoMiniDTO file2 = new ObjectInfoMiniDTO("file-2", TypeObject.File);
        ObjectInfoMiniDTO folder1 = new ObjectInfoMiniDTO("folder-1", TypeObject.Folder);
        ObjectInfoMiniDTO folder2 = new ObjectInfoMiniDTO("folder-2", TypeObject.Folder);

        objectInfoMiniDTOS.add(file1);
        objectInfoMiniDTOS.add(file2);
        objectInfoMiniDTOS.add(folder1);
        objectInfoMiniDTOS.add(folder2);

        Mockito.when(objectRepository.getObjectsNameByObjectLocation(fullDirectory, principal.getName())).thenReturn(objectInfoMiniDTOS);

        List<ObjectInfoMiniDTO> objects = objectService.getAllObjectsFromPath(fullDirectory, principal.getName());

        assertEquals(objectInfoMiniDTOS, objects);
    }

    @Test
    void testGetObjectsName_empty() {
        List<ObjectInfoMiniDTO> objectInfoMiniDTOS = new ArrayList<>();

        Mockito.when(objectRepository.getObjectsNameByObjectLocation(fullDirectory, principal.getName())).thenReturn(objectInfoMiniDTOS);

        List<ObjectInfoMiniDTO> objects = objectService.getAllObjectsFromPath(fullDirectory, principal.getName());

        assertEquals(objectInfoMiniDTOS, objects);
    }
}
