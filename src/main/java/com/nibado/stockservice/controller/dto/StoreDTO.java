package com.nibado.stockservice.controller.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class StoreDTO {
    private UUID id;
    private String name;
}
