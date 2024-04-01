package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.dto.response.account.AccountInfoDTO;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
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

    Principal principal_1, principal_2;

    @BeforeEach
    void setUp() {
        principal_1 = () -> "user-1";
        principal_2 = () -> "user-2";
    }

    @Test
    void testGetAccountDetails() {
        List<AccountInfoDTO> expectedAccountInfoDTOList = new ArrayList<>();
        expectedAccountInfoDTOList.add(new AccountInfoDTO(principal_1.getName(), 1000));

        when(accountRepository.getAccountDetailsByLogin(principal_1.getName())).thenReturn(expectedAccountInfoDTOList);

        List<AccountInfoDTO> actualAccountInfoDTOList = accountService.getAccountDetails(principal_1.getName());

        assertEquals(expectedAccountInfoDTOList, actualAccountInfoDTOList);
    }

    @Test
    void testReduceSizeStorage() {
        when(accountRepository.findSizeStorageByLogin(principal_1.getName())).thenReturn(10000);
        when(accountRepository.findSizeStorageByLogin(principal_2.getName())).thenReturn(1000);

        accountService.reduceSizeStorage(principal_1.getName(), 1000);
        verify(accountRepository).subtractBytesFromSizeStorage(principal_1.getName(), 1000);

        accountService.reduceSizeStorage(principal_2.getName(), 1000);
        verify(accountRepository).subtractBytesFromSizeStorage(principal_2.getName(), 1000);
    }

    @Test
    void testReduceSizeStorage_NoStorageSpaceRequiredException() {
        when(accountRepository.findSizeStorageByLogin(principal_1.getName())).thenReturn(100);

        assertThrows(NoStorageSpaceRequiredException.class, () -> {
            accountService.reduceSizeStorage(principal_1.getName(), 1000);
        });
    }

    @Test
    void testAddSizeStorage() {
        ObjectDetails objectDetails = new ObjectDetails();
        objectDetails.setObjectSize(10000);

        when(objectRepository.findObjectDetailsByType("file", TypeObject.File, "files/", principal_1.getName()))
                .thenReturn(objectDetails);

        accountService.addSizeStorage("file",principal_1.getName(), "files/");

        verify(accountRepository).updateSizeStorageByLogin(principal_1.getName(), 10000);
    }

}

