package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> findById(long id);

    List<Item> findAll(long sharerId);

    List<Item> findByText(String text);

    Item save(Item item);

    Item update(ItemDto itemDto, Item item);

    void deleteById(long sharerId, long id);

    void deleteAll();
}