package com.knightlia.particle.sandbox.exception;

import com.knightlia.particle.sandbox.model.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<String> errors = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        return new ErrorResponse(errors);
    }

    @ExceptionHandler(NicknameExistsException.class)
    public ErrorResponse handleNicknameExistsException(NicknameExistsException e) {
        return new ErrorResponse(singletonList(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        return new ErrorResponse(singletonList(e.getMessage()));
    }
}
