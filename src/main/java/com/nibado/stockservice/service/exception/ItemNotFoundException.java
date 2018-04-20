package com.nibado.stockservice.service.exception;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(final UUID itemId) {
        super(String.format("Item with id %s not found", itemId));
    }
}
