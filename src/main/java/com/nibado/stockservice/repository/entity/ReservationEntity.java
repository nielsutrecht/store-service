package com.nibado.stockservice.repository.entity;

import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
public class ReservationEntity {
    private final UUID storeId;
    private final UUID itemId;
    private final int amount;
    private final ZonedDateTime until;
}
