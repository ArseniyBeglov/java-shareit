package ru.practicum.shareit.booking.model.exception;

public class AccessException extends RuntimeException {
    public AccessException(String message) {
        super(message);
    }
}
