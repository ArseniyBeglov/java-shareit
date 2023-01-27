package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapperImpl implements ItemMapper {
    @Override
    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable());
    }

    @Override
    public Item fromDto(long ownerId, ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(), ownerId);
    }
}
