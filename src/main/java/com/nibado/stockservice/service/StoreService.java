package com.nibado.stockservice.service;

import com.nibado.stockservice.service.domain.Store;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableCollection;
import static java.util.UUID.randomUUID;

@Service
public class StoreService {
    private final Map<UUID, Store> stores = Stream.of(
            new Store(randomUUID(), "Gamma Maarssen"),
            new Store(randomUUID(), "Gamma Nieuwengein"),
            new Store(randomUUID(), "Karwei Nieuwengein")
    ).collect(Collectors.toMap(Store::getId, s -> s));

    public Collection<Store> findAll() {
        return unmodifiableCollection(stores.values());
    }

    public Optional<Store> get(final UUID id) {
        return Optional.ofNullable(stores.get(id));
    }
}
