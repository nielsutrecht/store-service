package com.nibado.stockservice.service;

import com.nibado.stockservice.repository.ItemRepository;
import com.nibado.stockservice.repository.ReservationRepository;
import com.nibado.stockservice.repository.SupplyRepository;
import com.nibado.stockservice.repository.entity.SupplyEntity;
import com.nibado.stockservice.service.domain.Item;
import com.nibado.stockservice.service.domain.Store;
import com.nibado.stockservice.service.domain.StoreItemSupply;
import com.nibado.stockservice.service.domain.StoreSupply;
import com.nibado.stockservice.service.exception.StoreNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class SupplyService {
    private final StoreService storeService;
    private final ItemRepository itemRepository;
    private final ReservationRepository reservationRepository;
    private final SupplyRepository supplyRepository;

    public SupplyService(
            final StoreService storeService,
            final ItemRepository itemRepository,
            final ReservationRepository reservationRepository,
            final SupplyRepository supplyRepository) {
        this.storeService = storeService;
        this.itemRepository = itemRepository;
        this.reservationRepository = reservationRepository;
        this.supplyRepository = supplyRepository;
    }

    public StoreSupply findStoreSupply(final UUID storeId) {
        Store store = storeService.get(storeId).orElseThrow(() -> new StoreNotFoundException(storeId));

        Collection<SupplyEntity> storeSupply = supplyRepository.findByStoreId(storeId);
        Map<UUID, Integer> storeReservations = reservationRepository.findReservations(storeId);
        Map<UUID, Item> items = itemRepository.findAll().stream().map(Item::of).collect(toMap(Item::getId, i -> i));

        List<StoreItemSupply> itemSupplies = storeSupply.stream()
                .map(s -> new StoreItemSupply(
                        items.get(s.getItemId()),
                        s.getAmount(),
                        storeReservations.getOrDefault(s.getItemId(), 0)))
                .collect(Collectors.toList());

        return new StoreSupply(store, itemSupplies);
    }
}
