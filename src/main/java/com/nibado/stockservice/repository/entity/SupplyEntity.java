package com.nibado.stockservice.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class SupplyEntity {
    private final UUID storeId;
    private final UUID itemId;
    private int amount;

    public void add(final int amount) {
        this.amount += amount;
    }
}
