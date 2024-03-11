package com.vianny.cloudstorageapi.exception.handlers;

import com.vianny.cloudstorageapi.exception.ResponseDataException;
import com.vianny.cloudstorageapi.exception.requiredException.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(UnauthorizedRequiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseDataException handlerUnauthorizedErrorException (UnauthorizedRequiredException exception) {
        return new ResponseDataException((HttpStatus) exception.getStatusCode(), exception.getReason());
    }

    @ExceptionHandler(UnregisteredRequiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDataException handlerUnregisteredErrorException (UnregisteredRequiredException exception) {
        return new ResponseDataException(HttpStatus.BAD_REQUEST, exception.getReason());
    }

    @ExceptionHandler(BadSyntaxRequiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDataException handlerUnregisteredErrorException (BadSyntaxRequiredException exception) {
        return new ResponseDataException(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(NotFoundRequiredException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseDataException handlerNotFoundErrorException (NotFoundRequiredException exception) {
        return new ResponseDataException(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(NoAccessRequiredException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseDataException handlerNoAccessErrorException (NoAccessRequiredException exception) {
        return new ResponseDataException(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(ConflictRequiredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseDataException handlerNoAccessErrorException (ConflictRequiredException exception) {
        return new ResponseDataException(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(NoContentRequiredException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseDataException handlerNoContentErrorException (NoContentRequiredException exception) {
        return new ResponseDataException(HttpStatus.NO_CONTENT, exception.getMessage());
    }

    @ExceptionHandler(ServerErrorRequiredException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDataException handlerServerErrorException (ServerErrorRequiredException exception) {
        return new ResponseDataException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
