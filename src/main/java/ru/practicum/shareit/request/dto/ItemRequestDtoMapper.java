package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

public interface ItemRequestDtoMapper {

    ItemRequestDto toDto(ItemRequest itemRequest);

    ItemRequest fromDto(ItemRequestDto itemRequestDto);
}
