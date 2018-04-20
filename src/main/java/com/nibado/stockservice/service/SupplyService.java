package com.nibado.stockservice.service;

import com.nibado.stockservice.repository.ItemRepository;
import com.nibado.stockservice.repository.ReservationRepository;
import com.nibado.stockservice.repository.SupplyRepository;
import com.nibado.stockservice.repository.entity.ItemEntity;
import com.nibado.stockservice.repository.entity.SupplyEntity;
import com.nibado.stockservice.service.domain.Item;
import com.nibado.stockservice.service.domain.Store;
import com.nibado.stockservice.service.domain.StoreItemSupply;
import com.nibado.stockservice.service.domain.StoreSupply;
import com.nibado.stockservice.service.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class SupplyService {
    private final StoreService storeService;
    private final ItemRepository itemRepository;
    private final ReservationRepository reservationRepository;
    private final SupplyRepository supplyRepository;

    private final Duration maxReservationDuration;

    public SupplyService(
            final StoreService storeService,
            final ItemRepository itemRepository,
            final ReservationRepository reservationRepository,
            final SupplyRepository supplyRepository,
            @Value("${service.reservations.max-duration}") final int maxReservationDuration) {

        this.storeService = storeService;
        this.itemRepository = itemRepository;
        this.reservationRepository = reservationRepository;
        this.supplyRepository = supplyRepository;
        this.maxReservationDuration = Duration.ofSeconds(maxReservationDuration);
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

    public StoreItemSupply findStoreSupply(final UUID storeId, final UUID itemId) {
        Store store = storeService.get(storeId).orElseThrow(() -> new StoreNotFoundException(storeId));
        Item item = itemRepository.findById(itemId).map(Item::of).orElseThrow(() -> new ItemNotFoundException(itemId));

        Optional<SupplyEntity> supplyEntity = supplyRepository.findByStoreIdAndItemId(store.getId(), item.getId());

        if (!supplyEntity.isPresent()) {
            return new StoreItemSupply(item, 0, 0);
        }

        int reservations = reservationRepository.findReservations(storeId).getOrDefault(item.getId(), 0);

        return new StoreItemSupply(item, supplyEntity.get().getAmount(), reservations);
    }

    public void changeStoreSupply(final UUID storeId, final UUID itemId, final int amount) {
        Store store = storeService.get(storeId).orElseThrow(() -> new StoreNotFoundException(storeId));
        ItemEntity item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));

        Optional<SupplyEntity> supplyEntity = supplyRepository.findByStoreIdAndItemId(store.getId(), item.getId());

        int reserved = reservationRepository.findReservations(storeId).getOrDefault(itemId, 0);

        int total = supplyEntity.map(s -> s.getAmount() + amount).orElse(amount);

        if (total  < 0) {
            throw new SupplyNotNegativeException();
        }
        if (total - reserved < 0) {
            throw new ItemHasReservationsException(reserved);
        }

        if (supplyEntity.isPresent()) {
            supplyEntity.get().add(amount);
        } else {
            supplyRepository.add(storeId, itemId, amount);
        }
    }

    public void deleteStoreSupply(final UUID storeId, final UUID itemId) {
        Store store = storeService.get(storeId).orElseThrow(() -> new StoreNotFoundException(storeId));
        ItemEntity item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));

        supplyRepository.deleteAll(store.getId(), item.getId());
        reservationRepository.deleteAll(store.getId(), item.getId());
    }

    public void reserveItem(final UUID storeId, final UUID itemId, final int reservationAmount, final int minutes) {
        Duration duration = Duration.ofMinutes(minutes);

        if(duration.isNegative() || duration.isZero() || maxReservationDuration.minus(duration).isNegative()) {
            throw new InvalidDurationException(maxReservationDuration);
        }

        StoreItemSupply itemSupply = findStoreSupply(storeId, itemId);

        int available = itemSupply.getAvailableAmount() - itemSupply.getReservedAmount();

        if(reservationAmount < 1 || reservationAmount > available) {
            throw new InvalidReservedAmountException();
        }

        reservationRepository.reserve(storeId, itemId, reservationAmount, duration);
    }
}
