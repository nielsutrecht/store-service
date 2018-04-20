package com.nibado.stockservice.controller;

import com.nibado.stockservice.controller.dto.ItemDTO;
import com.nibado.stockservice.service.SupplyService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/supply")
@Slf4j
public class SupplyController {
    private final SupplyService supplyService;

    public SupplyController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @GetMapping("/{store}")
    public SupplyResponse getSupply(@PathVariable("store") final UUID store) {
        log.info("Item supply for store {}", store);

        List<ItemDTO> items = supplyService.findStoreSupply(store).getItems().stream().map(ItemDTO::of).collect(Collectors.toList());

        return new SupplyResponse(store, items);
    }

    @Value
    private static class SupplyResponse {
        private UUID store;
        private List<ItemDTO> items;
    }
}
