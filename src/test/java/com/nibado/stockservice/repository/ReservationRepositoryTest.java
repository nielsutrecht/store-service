package com.nibado.stockservice.repository;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationRepositoryTest {
    private static final UUID STORE_ID = new UUID(1234, 1234);
    private static final UUID ITEM_ID = new UUID(4321, 4321);

    private ReservationRepository reservationRepository;

    @Before
    public void setup() {
        reservationRepository = new ReservationRepository();
    }

    @Test
    public void reservationTimeout() throws Exception {
        assertThat(reservationRepository.findReservations(STORE_ID)).isEmpty();

        reservationRepository.reserve(STORE_ID, ITEM_ID, 1, Duration.ofMillis(500));
        reservationRepository.reserve(STORE_ID, ITEM_ID, 2, Duration.ofMillis(500));

        assertThat(reservationRepository.findReservations(STORE_ID).keySet()).hasSize(1);
        assertThat(reservationRepository.findReservations(STORE_ID).get(ITEM_ID)).isEqualTo(3);

        Thread.sleep(1000);

        assertThat(reservationRepository.findReservations(STORE_ID).keySet()).hasSize(0);
    }

    @Test
    public void deleteAll() {
        assertThat(reservationRepository.findReservations(STORE_ID)).isEmpty();

        reservationRepository.reserve(STORE_ID, ITEM_ID, 1, Duration.ofMillis(500));
        reservationRepository.reserve(STORE_ID, ITEM_ID, 2, Duration.ofMillis(500));

        assertThat(reservationRepository.findReservations(STORE_ID).keySet()).hasSize(1);
        assertThat(reservationRepository.findReservations(STORE_ID).get(ITEM_ID)).isEqualTo(3);

        reservationRepository.deleteAll(STORE_ID, ITEM_ID);

        assertThat(reservationRepository.findReservations(STORE_ID).keySet()).hasSize(0);
    }
}