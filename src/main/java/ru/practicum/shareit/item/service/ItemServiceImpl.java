package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getAll(long ownerId) {
        log.debug("Request GET to /items");
        return itemRepository.findAll(ownerId)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(long id) {
        log.debug("Request GET to /items/{}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id = " + id + " is not found"));
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getByText(String text) {
        log.debug("Request GET to /items/search?text={}", text);
        return itemRepository.findByText(text)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(long ownerId, ItemDto itemDto) {
        log.debug("Request POST to /items, with ownerId = {}, id = {}, name = {}, description = {}, isAvailable = {}",
                ownerId, itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id = " + ownerId + " not found"));
        Item item = itemMapper.fromDto(ownerId, itemDto);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long ownerId, long id, ItemDto itemDto) {
        log.debug("Request PATCH to /items, with id = {}", id);
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id = " + ownerId + " not found"));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id = " + id + " not found"));
        itemDto.setId(id);
        if (item.getOwner() != ownerId) {
            throw new NotFoundException("Item with this id not found in this user");
        }
        return itemMapper.toDto(itemRepository.update(itemDto, item));
    }

    @Override
    public void deleteById(long ownerId, long id) {
        log.debug("Request DELETE to /items/{}", id);
        itemRepository.deleteById(ownerId, id);
    }

    @Override
    public void deleteAll() {
        log.debug("Request DELETE to /items)");
        itemRepository.deleteAll();
    }

}
