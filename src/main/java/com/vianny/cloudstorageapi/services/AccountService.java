package com.vianny.cloudstorageapi.services;

import com.vianny.cloudstorageapi.models.Account;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
import com.vianny.cloudstorageapi.repositories.FileRepository;
import com.vianny.cloudstorageapi.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService implements UserDetailsService {
    private AccountRepository accountRepository;
    private FileRepository fileRepository;
    @Autowired
    public void setUserRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    @Autowired
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findUserByLogin(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", username)
        ));

        return UserDetailsImpl.build(account);
    }

    @Transactional
    public void reduceSizeStorage(String login, int sizeObject) {
        accountRepository.subtractBytesFromSizeStorage(login,sizeObject);
    }
    @Transactional
    public void addSizeStorage(String filename, String login, String path) {
        int sizeObject = fileRepository.findByObjectNameAndObjectLocationAndAccount_Login(filename, path, login).getObjectSize();
        accountRepository.updateSizeStorageByLogin(login, sizeObject);
    }
}
