package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {
    ItemDto toDto(Item item);

    Item fromDto(long sharerId, ItemDto itemDto);
}
