package com.nibado.stockservice.service.exception;

import java.time.Duration;

public class InvalidDurationException extends RuntimeException {
    public InvalidDurationException(final Duration max) {
        super(String.format("Duration can't exceed %s and must be positive", max));
    }
}
