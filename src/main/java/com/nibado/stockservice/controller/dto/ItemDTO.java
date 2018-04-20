package com.nibado.stockservice.controller.dto;

import com.nibado.stockservice.service.domain.StoreItemSupply;
import lombok.Value;

import java.util.UUID;

@Value
public class ItemDTO {
    private UUID id;
    private String name;
    private int availableAmount;
    private int reservedAmount;

    public static ItemDTO of(final StoreItemSupply supply) {
        return new ItemDTO(
                supply.getItem().getId(),
                supply.getItem().getName(),
                supply.getAvailableAmount(),
                supply.getReservedAmount());
    }
}
