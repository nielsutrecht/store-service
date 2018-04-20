package com.nibado.stockservice.repository;

import com.nibado.stockservice.repository.entity.ReservationEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

@Repository
public class ReservationRepository {
    public List<ReservationEntity> reservations = new ArrayList<>();

    public void reserve(final UUID storeId, final UUID itemId, final int amount, final Duration duration) {
        reservations.add(new ReservationEntity(
                storeId,
                itemId,
                amount,
                now().plus(duration)
        ));
    }

    public Map<UUID, Integer> findReservations(final UUID storeId) {
        return reservations.stream()
                .filter(r -> r.getStoreId().equals(storeId))
                .filter(r -> now().isBefore(r.getUntil()))
                .collect(
                        groupingBy(ReservationEntity::getItemId, summingInt(ReservationEntity::getAmount)));
    }

    public void deleteAll(final UUID storeId, final UUID itemId) {
        reservations.removeIf(r -> r.getStoreId().equals(storeId) && r.getItemId().equals(itemId));
    }

    @Scheduled(fixedRate = 60_000) //One minute
    public void cleanup() {
        reservations.removeIf(r -> r.getUntil().isAfter(now()));
    }
}
