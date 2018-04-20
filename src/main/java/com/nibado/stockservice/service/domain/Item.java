package com.nibado.stockservice.service.domain;

import com.nibado.stockservice.repository.entity.ItemEntity;
import lombok.Value;

import java.util.UUID;

@Value
public class Item {
    private UUID id;
    private String name;

    public static Item of(final ItemEntity entity) {
        return new Item(entity.getId(), entity.getName());
    }
}
