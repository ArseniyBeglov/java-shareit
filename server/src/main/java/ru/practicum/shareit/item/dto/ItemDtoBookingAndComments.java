package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.item.comments.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoBookingAndComments {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingIdAndBookerId lastBooking;
    private BookingIdAndBookerId nextBooking;
    private List<CommentDto> comments;
}
