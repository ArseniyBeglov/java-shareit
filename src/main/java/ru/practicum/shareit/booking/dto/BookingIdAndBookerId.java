package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingIdAndBookerId {
    private Long id;
    private Long bookerId;
}
