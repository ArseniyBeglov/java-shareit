package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Item>> ownersWithItems = new HashMap<>();
    private long countId = 1;

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAll(long ownerId) {
        return new ArrayList<>(ownersWithItems.get(ownerId));
    }

    @Override
    public List<Item> findByText(String text) {
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        (i.getDescription().toLowerCase().contains(text.toLowerCase())) && i.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        item.setId(countId++);
        items.put(item.getId(), item);
        final Set<Item> itemSet =
                ownersWithItems.computeIfAbsent(item.getOwner(), k -> new HashSet<>());
        itemSet.add(item);
        return item;
    }

    @Override
    public Item update(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !item.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.replace(itemDto.getId(), item);
        return item;
    }

    @Override
    public void deleteById(long sharerId, long id) {
        ownersWithItems.get(sharerId).remove(items.remove(id));
    }

    @Override
    public void deleteAll() {
        items.clear();
        ownersWithItems.clear();
    }
}
