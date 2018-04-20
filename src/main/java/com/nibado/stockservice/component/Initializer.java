package com.nibado.stockservice.component;

import com.nibado.stockservice.repository.ItemRepository;
import com.nibado.stockservice.repository.SupplyRepository;
import com.nibado.stockservice.service.StoreService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Initializer {
    private final StoreService storeService;
    private final ItemRepository itemRepository;
    private final SupplyRepository supplyRepository;

    public Initializer(StoreService storeService, ItemRepository itemRepository, SupplyRepository supplyRepository) {
        this.storeService = storeService;
        this.itemRepository = itemRepository;
        this.supplyRepository = supplyRepository;
    }

    @PostConstruct
    public void initialSupply() {
        storeService.findAll().forEach(s -> supplyRepository.deleteAll(s.getId()));
        storeService.findAll().forEach(s -> itemRepository.findAll().forEach(i -> supplyRepository.add(s.getId(), i.getId(), 100)));
    }
}
