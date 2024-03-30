package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.dto.request.jwt.RequestJWT;
import com.vianny.cloudstorageapi.dto.response.ResponseMessage;
import com.vianny.cloudstorageapi.dto.authentication.SignInRequest;
import com.vianny.cloudstorageapi.dto.authentication.SignUpRequest;
import com.vianny.cloudstorageapi.exception.requiredException.ServerErrorRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.UnauthorizedRequiredException;
import com.vianny.cloudstorageapi.exception.requiredException.UnregisteredRequiredException;
import com.vianny.cloudstorageapi.models.Account;
import com.vianny.cloudstorageapi.repositories.AccountRepository;
import com.vianny.cloudstorageapi.services.MinioService;
import com.vianny.cloudstorageapi.utils.JwtCore;
import io.minio.errors.*;
import jakarta.validation.Valid;
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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/authAccount")
public class SecurityController {
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;

    private MinioService minioService;

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
    @Autowired
    public void setMinioService(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/signUp")
    ResponseEntity<?> singUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            if (accountRepository.existsUserByLogin(signUpRequest.getLogin())) {
                throw new UnregisteredRequiredException(HttpStatus.BAD_REQUEST, "Choose a different login");
            }

            Account account = new Account();
            String hashed = passwordEncoder.encode(signUpRequest.getPassword());

            account.setLogin(signUpRequest.getLogin());
            account.setPassword(hashed);

            minioService.createBucket(signUpRequest.getLogin());
            accountRepository.save(account);
        }
        catch (BadCredentialsException e) {
            throw new UnauthorizedRequiredException(HttpStatus.UNAUTHORIZED, "Registration failed");
        }
        catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
               NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
               InternalException e) {
            throw new ServerErrorRequiredException(e.getMessage());
        }

        ResponseMessage responseMessage = new ResponseMessage(HttpStatus.CREATED, "Successfully");
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @PostMapping("/signIn")
    ResponseEntity<?> singIn(@Valid @RequestBody SignInRequest singInRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(singInRequest.getLogin(),
                            singInRequest.getPassword()));


        }
        catch (BadCredentialsException e) {
            throw new UnauthorizedRequiredException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);

        RequestJWT requestJWT = new RequestJWT(HttpStatus.OK, jwt);
        return new ResponseEntity<>(requestJWT, HttpStatus.OK);
    }

}
