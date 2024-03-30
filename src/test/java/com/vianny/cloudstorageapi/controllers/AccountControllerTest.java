package com.vianny.cloudstorageapi.controllers;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.vianny.cloudstorageapi.dto.AccountDTO;
import com.vianny.cloudstorageapi.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @Mock
    private AccountService accountService;
    @InjectMocks
    private AccountController accountController;
    private MockMvc mockMvc;

    Principal principal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

        principal = () -> "user";
    }

    @Test
    void testGetPropertiesAccount() throws Exception {
        String testLogin = "user";

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(testLogin);

        List<AccountDTO> accountDTOList = new ArrayList<>();
        accountDTOList.add(new AccountDTO(principal.getName(), 1000));
        when(accountService.getAccountDetails(testLogin)).thenReturn(accountDTOList);

        mockMvc.perform(get("/myCloud/account/details").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus", is("FOUND")))
                .andExpect(jsonPath("$.details[0].login", is(testLogin)));
    }
}
