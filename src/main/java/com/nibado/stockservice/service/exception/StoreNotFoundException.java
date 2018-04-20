package com.nibado.stockservice.service.exception;

import java.util.UUID;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(final UUID storeId) {
        super(String.format("Store with id %s not found", storeId));
    }
}
