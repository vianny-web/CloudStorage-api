package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.dto.ObjectDetailsDTO;
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

    @Test
    void testGetObject() {
        List<ObjectDetailsDTO> objectDetailsDTOS = new ArrayList<>();
        ObjectDetailsDTO file1 = new ObjectDetailsDTO("file", TypeObject.File, "user-1/files/", 10, LocalDateTime.now());
        objectDetailsDTOS.add(file1);

        ObjectDetails objectDetails = new ObjectDetails("file", TypeObject.File, 10,"user-1/files/", LocalDateTime.now());

        Mockito.when(objectRepository.findObjectDetailsByType("file", TypeObject.File, "user-1/files/", principal.getName()))
                .thenReturn(objectDetails);
        Mockito.when(objectRepository.getObjectDetailsByObjectLocation("file", TypeObject.File, "user-1/files/", principal.getName())).thenReturn(objectDetailsDTOS);

        List<ObjectDetailsDTO> result = objectService.getObject("file", "user-1/files/", principal.getName());

        assertEquals(objectDetailsDTOS, result);
    }
    @Test
    void testGetObject_NotFoundRequiredException() {
        Mockito.when(objectRepository.findObjectDetailsByType("file", TypeObject.File, "user-1/files/", principal.getName()))
                .thenReturn(null);

        assertThrows(NotFoundRequiredException.class,() -> objectService.getObject("file", "user-1/files/", principal.getName()));
    }
}
