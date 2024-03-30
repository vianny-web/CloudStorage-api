package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.exception.requiredException.ConflictRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.NotFoundRequiredException;
import com.vianny.cloudstorageapi.models.Account;
import com.vianny.cloudstorageapi.models.ObjectDetails;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
import com.vianny.cloudstorageapi.repositories.ObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @Mock
    private ObjectRepository objectRepository;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private FileService fileService;

    Principal principal;
    String fullDirectory;
    String filename;
    MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        principal = () -> "user-1";
        fullDirectory = principal.getName() + "/" + "files/";
        filename = "file.txt";
        mockFile = new MockMultipartFile("file", filename, "", "Some file content".getBytes());
    }


    // Тестирование всех случаев метода "saveFile"
    @Test
    void testSaveFile() {
        Account account = new Account(principal.getName(), anyString());

        when(accountRepository.findUserByLogin(principal.getName())).thenReturn(Optional.of(account));
        when(objectRepository.findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(filename, TypeObject.File, fullDirectory, principal.getName())).thenReturn(null);

        fileService.saveFile(mockFile, fullDirectory, principal.getName());

        verify(objectRepository, times(1)).save(any(ObjectDetails.class));
    }
    @Test
    void testSaveFile_ConflictRequiredException() {
        when(objectRepository.findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(filename, TypeObject.File, fullDirectory, principal.getName())).thenReturn(new ObjectDetails());

        assertThrows(ConflictRequiredException.class,() -> fileService.saveFile(mockFile, fullDirectory, principal.getName()));
    }


    // Тестирование всех случаев метода "deleteFile"
    @Test
    void testDeleteFile() {
        ObjectDetails objectDetails = new ObjectDetails(filename, TypeObject.File, 100, fullDirectory, LocalDateTime.now());

        when(objectRepository.findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(filename, TypeObject.File, fullDirectory, principal.getName())).thenReturn(objectDetails);

        fileService.deleteFile(filename, fullDirectory, principal.getName());

        verify(objectRepository, times(1)).deleteByObjectLocation(objectDetails.getObjectName(), objectDetails.getObjectType(), objectDetails.getObjectLocation(), principal.getName());
    }
    @Test
    void testDeleteFile_NotFoundRequiredException() {
        when(objectRepository.findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(filename, TypeObject.File, fullDirectory, principal.getName())).thenReturn(null);

        assertThrows(NotFoundRequiredException.class,() -> fileService.deleteFile(filename, fullDirectory, principal.getName()));
    }
}
