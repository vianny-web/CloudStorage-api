package com.vianny.cloudstorageapi.controllers;

import com.vianny.cloudstorageapi.dto.ResponseMessage;
import com.vianny.cloudstorageapi.dto.request.RequestData;
import com.vianny.cloudstorageapi.services.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/myCloud")
public class MainController {

}
