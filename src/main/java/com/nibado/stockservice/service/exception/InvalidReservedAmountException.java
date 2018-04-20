package com.nibado.stockservice.service.exception;

public class InvalidReservedAmountException extends RuntimeException {
   public InvalidReservedAmountException() {
       super("Reserved amount invalid");
   }
}
