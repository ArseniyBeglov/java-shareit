package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.exception.CommentAccessException;
import ru.practicum.shareit.booking.model.exception.NotFoundException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentMapper;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingAndComments;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDtoBookingAndComments> getAll(long sharerId, int from, int size) {
        log.debug("Request GET to /items");
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " is not found"));

        List<Item> items = itemRepository.findAllByOwner_Id_OrderByIdAsc(sharerId,
                PageRequest.of(from / size, size));
        List<ItemDtoBookingAndComments> itemDtoWithBookingAndComments = new ArrayList<>();
        Set<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toSet());
        List<Comment> comments = commentRepository.findByItem_IdIn(itemsId);
        Map<Item, List<Comment>> commentsByItem = comments.stream()
                .collect(groupingBy(Comment::getItem, toList()));

        List<Booking> lastBookings = bookingRepository.findLastBookings(itemsId);
        Map<Item, List<Booking>> bookingsByItem = lastBookings.stream()
                .collect(groupingBy(Booking::getItem, toList()));

        List<Booking> nextBookings = bookingRepository.findNextBookings(itemsId);
        Map<Item, List<Booking>> bookingsByItem2 = nextBookings.stream()
                .collect(groupingBy(Booking::getItem, toList()));

        fillItemDtoWithBookingAndComments(items, itemDtoWithBookingAndComments, commentsByItem, bookingsByItem,
                bookingsByItem2);

        return itemDtoWithBookingAndComments;
    }

    @Override
    public ItemDtoBookingAndComments getById(long sharerId, long id) {
        log.debug("Request GET to /items/{}", id);
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " is not found"));
        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + id + " is not found"));

        return getItemDtoWithBookingAndComments(sharerId, item);
    }

    @Override
    public List<ItemDto> getByText(String text, int from, int size) {
        log.debug("Request GET to /items/search?text={}", text);
        return itemRepository.findByText(text, PageRequest.of(from / size, size))
                .stream()
                .map(itemMapper::toDto)
                .collect(toList());
    }

    @Override
    public ItemDto create(long sharerId, ItemDtoInput itemDto) {
        log.debug("Request POST to /items, with sharerId = {}, id = {}, name = {}, description = {}, isAvailable = {}",
                sharerId, itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        User owner = userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " is not found"));
        Long requestId = itemDto.getRequestId();
        ItemRequest itemRequest = null;

        if (requestId != null) {
            itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() ->
                            new NotFoundException("This itemRequest not be found"));
        }
        Item item = itemMapper.fromDtoInput(itemDto, owner, itemRequest);
        item.setOwner(owner);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(long sharerId, long id, ItemDto itemDto) {
        log.debug("Request PATCH to /items/{}", id);
        userRepository.findById(sharerId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + sharerId + " is not found"));
        Item item = itemRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + id + " is not found"));

        if (!item.getOwner().getId().equals(sharerId)) {
            throw new NotFoundException("Item with this id is not found in this user");
        }
        itemDto.setId(id);

        return itemMapper.toDto(update(itemDto, item));
    }

    @Override
    public void deleteById(long sharerId, long id) {
        log.debug("Request DELETE to /items/{}", id);
        if (!itemRepository.existsById(id) || !userRepository.existsById(sharerId)) {
            throw new NotFoundException("user or item id is not correct");
        }
        itemRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.debug("Request DELETE to /items)");
        itemRepository.deleteAll();
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        log.debug("Request POST to /items/{}/comment", itemId);
        User author = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + userId + " is not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundException("Item with id = " + itemId + " is not found"));

        List<Booking> bookings = bookingRepository.findBookingsByBooker_IdAndItem_IdAndEndIsBefore(userId,
                itemId, LocalDateTime.now());
        if (bookings.stream().findAny().isEmpty()) {
            throw new CommentAccessException("User is not booked this item");
        }

        Comment comment = commentMapper.fromDto(commentDto, item, author);
        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    private Item update(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !item.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    private ItemDtoBookingAndComments getItemDtoWithBookingAndComments(long sharerId, Item item) {
        BookingIdAndBookerId lastBooking = null;
        BookingIdAndBookerId nextBooking = null;
        if (item.getOwner().getId() == sharerId) {
            lastBooking = bookingRepository.findFirstByItem_IdAndStartBeforeOrderByEndDesc(item.getId(),
                            LocalDateTime.now())
                    .map(bookingMapper::toDtoOnlyIdAndBookerId)
                    .orElse(null);
            nextBooking = bookingRepository.findFirstByItem_IdAndStartAfterOrderByEndDesc(item.getId(),
                            LocalDateTime.now())
                    .map(bookingMapper::toDtoOnlyIdAndBookerId)
                    .orElse(null);
        }
        List<CommentDto> comments = commentRepository.findByItem_Id(item.getId()).stream()
                .map(commentMapper::toDto)
                .collect(toList());
        return itemMapper.toDtoWithBookingAndComments(item, lastBooking, nextBooking, comments);
    }

    private void fillItemDtoWithBookingAndComments(List<Item> items,
                                                   List<ItemDtoBookingAndComments> itemDtoWithBookingAndComments,
                                                   Map<Item, List<Comment>> commentsByItem,
                                                   Map<Item, List<Booking>> bookingsByItem,
                                                   Map<Item, List<Booking>> bookingsByItem2) {
        for (Item item : items) {
            List<CommentDto> commentsDto = null;
            BookingIdAndBookerId lastBooking = null;
            BookingIdAndBookerId nextBooking = null;

            if (commentsByItem.size() != 0) {
                commentsDto = commentsByItem.get(item).stream()
                        .map(commentMapper::toDto)
                        .collect(toList());
            }

            if (bookingsByItem.get(item) != null) {
                List<Booking> lastBookings = bookingsByItem.get(item);

                if (lastBookings.size() != 0) {
                    lastBooking = bookingMapper.toDtoOnlyIdAndBookerId(lastBookings.get(0));
                }
            }

            if (bookingsByItem2.get(item) != null) {
                List<Booking> nextBookings = bookingsByItem2.get(item);

                if (nextBookings.size() != 0) {
                    nextBooking = bookingMapper.toDtoOnlyIdAndBookerId(nextBookings.get(0));
                }
            }

            itemDtoWithBookingAndComments.add(itemMapper.toDtoWithBookingAndComments(
                    item, lastBooking, nextBooking, commentsDto
            ));
        }
    }
}
