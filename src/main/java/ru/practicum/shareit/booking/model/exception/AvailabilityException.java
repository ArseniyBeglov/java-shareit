package ru.practicum.shareit.booking.model.exception;

public class AvailabilityException extends RuntimeException {
    public AvailabilityException(String message) {
        super(message);
    }
}
