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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FolderServiceTest {
    @Mock
    private ObjectRepository objectRepository;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private FolderService folderService;

    Principal principal;
    String fullDirectory;
    String foldername;

    @BeforeEach
    void setUp() {
        principal = () -> "user-1";
        fullDirectory = principal.getName() + "/" + "files/";
        foldername = "photos";
    }


    // Тестирование всех случаев метода "saveFolder"
    @Test
    void testSaveFile() {
        Account account = new Account(principal.getName(), anyString());

        when(accountRepository.findUserByLogin(principal.getName())).thenReturn(Optional.of(account));
        when(objectRepository.findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(foldername, TypeObject.Folder, fullDirectory, principal.getName())).thenReturn(null);

        folderService.saveFolder(foldername, fullDirectory, principal.getName());

        verify(objectRepository, times(1)).save(any(ObjectDetails.class));
    }
    @Test
    void testSaveFolder_ConflictRequiredException() {
        when(objectRepository.findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(foldername, TypeObject.Folder, fullDirectory, principal.getName())).thenReturn(new ObjectDetails());

        assertThrows(ConflictRequiredException.class,() -> folderService.saveFolder(foldername, fullDirectory, principal.getName()));
    }


    // Тестирование всех случаев метода "deleteFolder"
    @Test
    void testDeleteFolder() {
        ObjectDetails objectDetails = new ObjectDetails(foldername, TypeObject.Folder, 0, fullDirectory, LocalDateTime.now());

        when(objectRepository.findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(foldername, TypeObject.Folder, fullDirectory, principal.getName())).thenReturn(objectDetails);

        folderService.deleteFolder(foldername, fullDirectory, principal.getName());

        verify(objectRepository, times(1)).deleteByObjectLocation(objectDetails.getObjectName(), objectDetails.getObjectType(), objectDetails.getObjectLocation(), principal.getName());
    }
    @Test
    void testDeleteFolder_NotFoundRequiredException() {
        when(objectRepository.findByObjectNameAndObjectTypeAndObjectLocationAndAccount_Login(foldername, TypeObject.Folder, fullDirectory, principal.getName())).thenReturn(null);

        assertThrows(NotFoundRequiredException.class,() -> folderService.deleteFolder(foldername, fullDirectory, principal.getName()));
    }
}
