package com.nibado.stockservice.service.domain;

import lombok.Value;

import java.util.UUID;

@Value
public class Store {
    private UUID id;
    private String name;
}
