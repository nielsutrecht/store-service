package com.nibado.stockservice.service.exception;

public class SupplyNotNegativeException extends RuntimeException {
   public SupplyNotNegativeException() {
       super("Supply can't be negative");
   }
}
