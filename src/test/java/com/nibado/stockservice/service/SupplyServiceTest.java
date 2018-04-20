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
import com.nibado.stockservice.service.exception.StoreNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SupplyServiceTest {
    private static final UUID STORE_ID = new UUID(1234, 1234);
    private static final UUID ITEM_ID = new UUID(4321, 4321);

    private static final Store STORE = new Store(STORE_ID, "Test store");

    private SupplyService supplyService;

    private StoreService storeService;
    private ItemRepository itemRepository;
    private ReservationRepository reservationRepository;
    private SupplyRepository supplyRepository;

    @Before
    public void setup() {
        storeService = mock(StoreService.class);
        itemRepository = mock(ItemRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        supplyRepository = mock(SupplyRepository.class);

        supplyService = new SupplyService(
                storeService,
                itemRepository,
                reservationRepository,
                supplyRepository,
                1800
        );
    }

    @Test
    public void findStoreSupply() {
        Map<UUID, Integer> reservations = new HashMap<>();
        reservations.put(ITEM_ID, 20);
        when(storeService.get(STORE_ID)).thenReturn(Optional.of(STORE));
        when(itemRepository.findAll()).thenReturn(Arrays.asList(new ItemEntity(ITEM_ID, "Test item")));
        when(reservationRepository.findReservations(STORE_ID)).thenReturn(reservations);
        when(supplyRepository.findByStoreId(STORE_ID)).thenReturn(Arrays.asList(new SupplyEntity(STORE_ID, ITEM_ID, 100)));

        StoreSupply storeSupply = supplyService.findStoreSupply(STORE_ID);

        assertThat(storeSupply.getStore()).isEqualTo(STORE);
        assertThat(storeSupply.getItems()).hasSize(1);
        assertThat(storeSupply.getItems().get(0)).isEqualTo(new StoreItemSupply(new Item(ITEM_ID, "Test item"), 100, 20));
    }

    @Test(expected = StoreNotFoundException.class)
    public void findStoreSupply_StoreNotFound() {
        when(storeService.get(STORE_ID)).thenThrow(new StoreNotFoundException(STORE_ID));

        supplyService.findStoreSupply(STORE_ID);
    }

    @Test(expected = StoreNotFoundException.class)
    public void findStoreSupply_StoreAndItem_StoreNotFound() {
        when(storeService.get(STORE_ID)).thenThrow(new StoreNotFoundException(STORE_ID));

        supplyService.findStoreSupply(STORE_ID, ITEM_ID);
    }
}