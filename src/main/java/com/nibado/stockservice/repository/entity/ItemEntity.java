package com.nibado.stockservice.repository.entity;

import lombok.Value;

import java.util.UUID;

@Value
public class ItemEntity {
    private UUID id;
    private String name;
}
