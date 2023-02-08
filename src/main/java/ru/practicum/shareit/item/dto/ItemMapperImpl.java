package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapperImpl implements ItemMapper {

    private final UserMapper userDtoConverter;
    private final ItemRequestDtoMapper itemRequestDtoConverter;

    @Override
    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(),
                userDtoConverter.toDto(item.getOwner()),
                item.getRequest() == null ? null :
                        itemRequestDtoConverter.toDto(item.getRequest()));
    }

    @Override
    public Item fromDto(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(),
                owner, itemDto.getRequest() == null ?
                null : itemRequestDtoConverter.fromDto(itemDto.getRequest()));
    }

    @Override
    public Item fromDtoInput(ItemDtoInput itemDto, User owner) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(),
                owner, null);
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
}
