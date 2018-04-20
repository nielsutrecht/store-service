package com.nibado.stockservice.service.domain;

import lombok.Value;

@Value
public class StoreItemSupply {
    private Item item;
    private int availableAmount;
    private int reservedAmount;
}
