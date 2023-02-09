package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutput;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestOutput> getAll(long requestorId);

    ItemRequestOutput getById(long userId, long requestId);

    List<ItemRequestOutput> getAllAnotherUsers(long requestorId, int from, int size);

    ItemRequestOutput create(long requestorId, ItemRequestDto itemRequest);
}
