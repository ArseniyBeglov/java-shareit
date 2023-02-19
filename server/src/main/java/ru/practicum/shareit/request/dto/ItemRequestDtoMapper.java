package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDtoRequests;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRequestDtoMapper {

    ItemRequestOutput toDtoOutput(ItemRequest itemRequest, List<ItemDtoRequests> items);

    ItemRequest fromDtoInput(ItemRequestDto itemRequest, User owner);
}
