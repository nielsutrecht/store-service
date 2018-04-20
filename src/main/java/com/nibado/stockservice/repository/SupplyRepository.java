package com.nibado.stockservice.repository;

import com.nibado.stockservice.repository.entity.SupplyEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Repository
public class SupplyRepository {
    private final List<SupplyEntity> supplies = new ArrayList<>();

    public Collection<SupplyEntity> findByStoreId(final UUID storeId) {
        return supplies.stream()
                .filter(s -> s.getStoreId().equals(storeId))
                .collect(toList());
    }
}
