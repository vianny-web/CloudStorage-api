package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.dto.RequestJWT;
import com.vianny.cloudstorageapi.dto.ResponseMessage;
import com.vianny.cloudstorageapi.dto.authentication.SignInRequest;
import com.vianny.cloudstorageapi.dto.authentication.SignUpRequest;
import com.vianny.cloudstorageapi.exception.requiredException.UnauthorizedRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.UnregisteredRequiredException;
import com.vianny.cloudstorageapi.models.Account;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
import com.vianny.cloudstorageapi.utils.JwtCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authAccount")
public class SecurityController {
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;

    @Autowired
    public void setUserRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }

    @PostMapping("/signUp")
    ResponseEntity<?> singUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            if (accountRepository.existsUserByLogin(signUpRequest.getLogin())) {
                throw new UnregisteredRequiredException(HttpStatus.BAD_REQUEST, "Выберете другой логин");
            }

            Account account = new Account();
            String hashed = passwordEncoder.encode(signUpRequest.getPassword());

            account.setLogin(signUpRequest.getLogin());
            account.setPassword(hashed);
            accountRepository.save(account);
        }
        catch (BadCredentialsException e) {
            throw new UnauthorizedRequiredException(HttpStatus.UNAUTHORIZED, "Регистрация не удалась");
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "Успешно");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @PostMapping("/signIn")
    ResponseEntity<?> singIn(@RequestBody SignInRequest singInRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(singInRequest.getLogin(),
                            singInRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
            throw new UnauthorizedRequiredException(HttpStatus.UNAUTHORIZED, "Аутентификация не удалась");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);

        RequestJWT requestJWT = new RequestJWT(HttpStatus.OK, jwt);
        return new ResponseEntity<>(requestJWT, HttpStatus.OK);
    }

}
