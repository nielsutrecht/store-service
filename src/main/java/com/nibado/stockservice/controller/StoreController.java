package com.nibado.stockservice.controller;

import com.nibado.stockservice.service.StoreService;
import com.nibado.stockservice.service.domain.Store;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/store")
@Slf4j
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }


    @GetMapping
    public StoreResponse stores() {
        log.info("Get all stores");

        return new StoreResponse(storeService.findAll());
    }

    @Value
    private static class StoreResponse {
        private Collection<Store> stores;
    }
}
