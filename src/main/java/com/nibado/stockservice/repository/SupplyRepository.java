package com.nibado.stockservice.repository;

import com.nibado.stockservice.repository.entity.SupplyEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Repository
public class SupplyRepository {
    private final List<SupplyEntity> supplies = new ArrayList<>();

    public Collection<SupplyEntity> findByStoreId(final UUID storeId) {
        return supplies.stream()
                .filter(s -> s.getStoreId().equals(storeId))
                .collect(toList());
    }

    public Optional<SupplyEntity> findByStoreIdAndItemId(final UUID storeId, final UUID itemId) {
        return supplies.stream()
                .filter(s -> s.getStoreId().equals(storeId))
                .filter(s -> s.getItemId().equals(itemId))
                .findAny();
    }

    public void deleteAll(final UUID storeId, final UUID itemId) {
        supplies.removeIf(s -> s.getStoreId().equals(storeId) && s.getItemId().equals(itemId));
    }

    public void deleteAll(final UUID storeId) {
        supplies.removeIf(s -> s.getStoreId().equals(storeId));
    }

    public void add(final UUID storeId, final UUID itemId, final int amount) {
        supplies.add(new SupplyEntity(storeId, itemId, amount));
    }
}
