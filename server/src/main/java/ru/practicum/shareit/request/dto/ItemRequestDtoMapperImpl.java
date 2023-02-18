package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoRequests;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoIdAndName;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestDtoMapperImpl implements ItemRequestDtoMapper {

    @Override
    public ItemRequestOutput toDtoOutput(ItemRequest itemRequest, List<ItemDtoRequests> items) {
        return new ItemRequestOutput(itemRequest.getId(), itemRequest.getDescription(),
                new UserDtoIdAndName(itemRequest.getRequestor().getId(),
                        itemRequest.getRequestor().getName()),
                itemRequest.getCreated(), items);
    }

    @Override
    public ItemRequest fromDtoInput(ItemRequestDto itemRequest, User owner) {
        return new ItemRequest(itemRequest.getId(), itemRequest.getDescription(),
                owner, itemRequest.getCreated() == null ? LocalDateTime.now() : itemRequest.getCreated());
    }
}
