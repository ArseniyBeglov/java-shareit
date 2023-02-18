package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingAndComments;
import ru.practicum.shareit.item.dto.ItemDtoInput;

import java.util.List;

public interface ItemService {
    List<ItemDtoBookingAndComments> getAll(long sharerId, int from, int size);

    ItemDtoBookingAndComments getById(long sharerId, long id);

    List<ItemDto> getByText(String text, int from, int size);

    ItemDto create(long sharerId, ItemDtoInput itemDto);

    ItemDto update(long sharerId, long id, ItemDto itemDto);

    void deleteById(long sharerId, long id);

    void deleteAll();

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}
