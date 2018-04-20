package com.nibado.stockservice.service.domain;

import lombok.Value;

import java.util.List;

@Value
public class StoreSupply {
    private Store store;
    private List<StoreItemSupply> items;
}
