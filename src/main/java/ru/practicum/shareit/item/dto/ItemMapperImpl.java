package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapperImpl implements ItemMapper {

    private final UserMapper userMapper;


    @Override
    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(),
                userMapper.toDto(item.getOwner()),
                item.getRequest() == null ? null :
                        item.getRequest().getId());
    }


    @Override
    public Item fromDtoInput(ItemDtoInput itemDto, User owner, ItemRequest itemRequest) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(),
                owner, itemRequest);
    }

    @Override
    public ItemDtoBookingAndComments toDtoWithBookingAndComments(Item item,
                                                                 BookingIdAndBookerId lastBooking,
                                                                 BookingIdAndBookerId nextBooking,
                                                                 List<CommentDto> comments) {
        return new ItemDtoBookingAndComments(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(),
                lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDtoRequests> toDtoListForRequest(List<Item> items) {
        return items.stream()
                .map(ItemMapperImpl::toDtoForRequest)
                .collect(Collectors.toList());
    }

    public static ItemDtoRequests toDtoForRequest(Item item) {
        return new ItemDtoRequests(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getRequest().getId());
    }
}
