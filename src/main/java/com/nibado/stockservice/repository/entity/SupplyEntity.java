package com.nibado.stockservice.repository.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class SupplyEntity {
    private final UUID storeId;
    private final UUID itemId;
    private int amount;
}
