package com.nibado.stockservice.repository;

import com.nibado.stockservice.repository.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.util.Collections.unmodifiableCollection;
import static java.util.UUID.randomUUID;

@Repository
public class ItemRepository {
    private final List<ItemEntity> items = Arrays.asList(
            new ItemEntity(randomUUID(), "Schep"),
            new ItemEntity(randomUUID(), "Lamp"),
            new ItemEntity(randomUUID(), "Boormachine"),
            new ItemEntity(randomUUID(), "Motorolie"),
            new ItemEntity(randomUUID(), "Tuinbankje"),
            new ItemEntity(randomUUID(), "BBQ")
            );

    public Collection<ItemEntity> findAll() {
        return unmodifiableCollection(items);
    }

    public Optional<ItemEntity> findByName(final String name) {
        return items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findAny();
    }

    public Optional<ItemEntity> findById(final UUID id) {
        return items.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
    }
}
