package com.nibado.stockservice.service.exception;

public class ItemHasReservationsException extends RuntimeException {
    public ItemHasReservationsException(final int reservations) {
        super(String.format("Item still has %s reservations", reservations));
    }
}
