package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoRequests;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapperImpl;
import ru.practicum.shareit.request.dto.ItemRequestOutput;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestDtoMapper itemRequestDtoMapper = new ItemRequestDtoMapperImpl();
    private final ItemMapper itemMapper = new ItemMapperImpl(new UserMapperImpl());

    @Override
    public List<ItemRequestOutput> getAll(long requestorId) {
        if (!userRepository.existsById(requestorId)) {
            throw new NotFoundException("User with id = " + requestorId + " not found");
        }
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_Id(requestorId);

        return getItemRequestsDtoWithItemsFromRequests(requests);
    }

    @Override
    public ItemRequestOutput getById(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException("Request with id = " + requestId + " not found");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();

        List<Item> items = new ArrayList<>(itemRepository.findByRequest_IdOrderById(requestId));

        return itemRequestDtoMapper.toDtoOutput(itemRequest, itemMapper.toDtoListForRequest(items));
    }

    @Override
    public List<ItemRequestOutput> getAllAnotherUsers(long requestorId, int from, int size) {
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdNot(requestorId,
                PageRequest.of(from / size, size,
                        Sort.by(Sort.Direction.DESC, "created")));

        return getItemRequestsDtoWithItemsFromRequests(requests);
    }

    @Override
    public ItemRequestOutput create(long requestorId, ItemRequestDto itemRequestDto) {
        if (!userRepository.existsById(requestorId)) {
            throw new NotFoundException("User with id = " + requestorId + " not found");
        }
        User owner = userRepository.findById(requestorId).get();
        ItemRequest itemRequest = itemRequestDtoMapper.fromDtoInput(itemRequestDto, owner);
        itemRequestRepository.save(itemRequest);

        return itemRequestDtoMapper.toDtoOutput(itemRequest, null);
    }

    private List<ItemRequestOutput> getItemRequestsDtoWithItemsFromRequests(List<ItemRequest> requests) {
        List<Long> requestsId = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findByRequest_IdIn(requestsId);
        Map<ItemRequest, List<Item>> itemRequestsByItem = items.stream()
                .collect(groupingBy(Item::getRequest, toList()));
        List<ItemRequestOutput> itemRequestOutputs = new ArrayList<>();

        for (ItemRequest itemRequest : requests) {
            List<Item> itemsTemp = itemRequestsByItem.getOrDefault(itemRequest, new ArrayList<>());
            List<ItemDtoRequests> itemDtoForRequests = itemsTemp.stream()
                    .map(itemMapper::toDtoForRequest)
                    .collect(toList());
            itemRequestOutputs.add(itemRequestDtoMapper.toDtoOutput(itemRequest, itemDtoForRequests));
        }

        return itemRequestOutputs;
    }
}
