package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.config.MinioConfig;
import com.vianny.cloudstorageapi.dto.AccountDTO;
import com.vianny.cloudstorageapi.dto.response.ResponseAccountDetails;
import com.vianny.cloudstorageapi.exception.requiredException.ServerErrorRequiredException;
import com.vianny.cloudstorageapi.services.AccountService;
import com.vianny.cloudstorageapi.services.FileService;
import com.vianny.cloudstorageapi.services.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/myCloud")
public class AccountController {
    private AccountService accountService;
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account/details")
    public ResponseEntity<ResponseAccountDetails<List<AccountDTO>>> getPropertiesFile(Principal principal) {
        try {
            List<AccountDTO> accountDetails = accountService.getAccountDetails(principal.getName());
            ResponseAccountDetails<List<AccountDTO>> dataObject = new ResponseAccountDetails<>(HttpStatus.FOUND, accountDetails);
            return new ResponseEntity<>(dataObject,HttpStatus.OK);
        }
        catch (Exception e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }
    }
}
