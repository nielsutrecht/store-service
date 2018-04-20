package com.nibado.stockservice.controller;

import com.nibado.stockservice.service.exception.StoreNotFoundException;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ErrorHandlers {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final Throwable ex) {
        return new Error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StoreNotFoundException.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final StoreNotFoundException ex) {
        return new Error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final MethodArgumentTypeMismatchException ex) {
        return new Error(ex.getMessage());
    }

    @Value
    public static class Error {
        private String message;
    }
}
