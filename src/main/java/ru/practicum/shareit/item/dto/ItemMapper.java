package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemMapper {
    ItemDto toDto(Item item);

    Item fromDto(ItemDto itemDto, User owner);

    Item fromDtoInput(ItemDtoInput itemDto, User owner);

    ItemDtoBookingAndComments toDtoWithBookingAndComments(Item item,
                                                          BookingIdAndBookerId lastBooking,
                                                          BookingIdAndBookerId nextBooking,
                                                          List<CommentDto> comments);
}
