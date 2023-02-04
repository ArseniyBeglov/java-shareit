package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDtoIdAndName;
import ru.practicum.shareit.user.dto.UserDtoIdAndName;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoOutput {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoIdAndName item;
    private UserDtoIdAndName booker;
    private Status status;
}
