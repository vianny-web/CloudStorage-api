package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.dto.AccountDTO;
import com.vianny.cloudstorageapi.enums.TypeObject;
import com.vianny.cloudstorageapi.exception.requiredException.NoStorageSpaceRequiredException;
import com.vianny.cloudstorageapi.models.ObjectDetails;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
import com.vianny.cloudstorageapi.repositories.ObjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ObjectRepository objectRepository;
    @InjectMocks
    private AccountService accountService;


    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldReturnAccountDetailsSuccessfully() {
        List<AccountDTO> expectedAccountDTOList = new ArrayList<>();
        expectedAccountDTOList.add(new AccountDTO("user", 1000));

        when(accountRepository.getAccountDetailsByLogin("user")).thenReturn(expectedAccountDTOList);

        List<AccountDTO> actualAccountDTOList = accountService.getAccountDetails("user");

        assertEquals(expectedAccountDTOList, actualAccountDTOList);
    }

    @Test
    void shouldReduceSizeStorageSuccessfullyWhenSpaceIsAvailable() {
        when(accountRepository.findSizeStorageByLogin("test1")).thenReturn(10000);
        when(accountRepository.findSizeStorageByLogin("test2")).thenReturn(1000);

        accountService.reduceSizeStorage("test1", 1000);
        verify(accountRepository).subtractBytesFromSizeStorage("test1", 1000);

        accountService.reduceSizeStorage("test2", 1000);
        verify(accountRepository).subtractBytesFromSizeStorage("test2", 1000);
    }

    @Test
    void shouldThrowNoStorageSpaceRequiredExceptionWhenSpaceIsNotAvailable() {
        when(accountRepository.findSizeStorageByLogin("test")).thenReturn(100);

        assertThrows(NoStorageSpaceRequiredException.class, () -> {
            accountService.reduceSizeStorage("test", 1000);
        });
    }

    @Test
    void shouldAddSizeStorageSuccessfully() {
        ObjectDetails objectDetails = new ObjectDetails();
        objectDetails.setObjectSize(10000);

        when(objectRepository.findObjectDetailsByType("nameTest", TypeObject.File, "/test1/", "test1"))
                .thenReturn(objectDetails);

        accountService.addSizeStorage("nameTest","test1", "/test1/");

        verify(accountRepository).updateSizeStorageByLogin("test1", 10000);
    }

}

