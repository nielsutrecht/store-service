package com.nibado.stockservice.controller;

import com.nibado.stockservice.service.exception.*;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class ErrorHandlers {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final Throwable ex) {
        log.error("Unknown error", ex);
        return new Error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StoreNotFoundException.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final StoreNotFoundException ex) {
        log.error("Store not found", ex);
        return new Error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final ItemNotFoundException ex) {
        log.error("Item not found", ex);
        return new Error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final MethodArgumentTypeMismatchException ex) {
        log.error("Can't parse path variable", ex);
        return new Error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SupplyNotNegativeException.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final SupplyNotNegativeException ex) {
        log.error("Supply can't be negative", ex);
        return new Error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidReservedAmountException.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final InvalidReservedAmountException ex) {
        log.error("Reserved amount too high", ex);
        return new Error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDurationException.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final InvalidDurationException ex) {
        log.error("Invalid duration", ex);
        return new Error(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ItemHasReservationsException.class)
    @ResponseBody
    public Error handleThrowableSafetyNet(final ItemHasReservationsException ex) {
        log.error("Item has reservations", ex);
        return new Error(ex.getMessage());
    }

    @Value
    public static class Error {
        private String message;
    }
}
