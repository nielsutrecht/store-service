package com.nibado.stockservice.controller;

import com.nibado.stockservice.controller.dto.ItemDTO;
import com.nibado.stockservice.service.SupplyService;
import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{store}/{item}")
    public ItemDTO getSupply(@PathVariable("store") final UUID store, @PathVariable("item") final UUID item) {
        log.info("Item supply for store {} and item {}", store, item);

        return ItemDTO.of(supplyService.findStoreSupply(store, item));
    }

    @PatchMapping("/{store}/{item}/amount")
    public ResponseEntity<?> changeAmount(
            @PathVariable("store") final UUID store,
            @PathVariable("item") final UUID item,
            @RequestBody AmountRequest amountRequest) {
        log.info("Change amount of {} for store {} with {}", item, store, amountRequest.amount);

        supplyService.changeStoreSupply(store, item, amountRequest.amount);

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{store}/{item}")
    public ResponseEntity<?> changeAmount(
            @PathVariable("store") final UUID store,
            @PathVariable("item") final UUID item) {
        log.info("Remove all of {} for store {} ", item, store);

        supplyService.deleteStoreSupply(store, item);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{store}/{item}/reservation")
    public ResponseEntity<?> addReservation(
            @PathVariable("store") final UUID store,
            @PathVariable("item") final UUID item,
            @RequestBody ReservationRequest reservationRequest) {
        log.info(
                "Adding reservation for {} of item {} for store {} with {} and a duration of {} minutes",
                item,
                store,
                reservationRequest.amount,
                reservationRequest.durationInMinutes);

        supplyService.reserveItem(store, item, reservationRequest.amount, reservationRequest.durationInMinutes);

        return ResponseEntity.accepted().build();
    }

    @Value
    private static class SupplyResponse {
        private UUID store;
        private List<ItemDTO> items;
    }

    @Data
    private static class AmountRequest {
        private int amount;
    }

    @Data
    private static class ReservationRequest {
        private int amount;
        private int durationInMinutes;
    }
}
