package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.exception.CommentAccessException;
import ru.practicum.shareit.booking.model.exception.NotFoundException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingAndComments;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private final Set<Long> itemIds = new HashSet<>(Arrays.asList(6L, 7L));
    private final User userOleg = new User(1L, "Oleg", "oleg@yandex.ru");
    private final UserDto userDtoOleg = new UserDto(userOleg.getId(), userOleg.getName(), userOleg.getEmail());
    private final User userIrina = new User(2L, "Irina", "irina@yandex.ru");
    private final UserDto userDtoIrina = new UserDto(userIrina.getId(), userIrina.getName(), userIrina.getEmail());
    private final ItemRequest request = new ItemRequest(4L, "I want this dryer!", userIrina,
            LocalDateTime.of(2023, 1, 20, 12, 0, 0));
    private final Item dryer = new Item(3L, "Dryer", "For curly hair",
            true, userIrina, request);
    private final ItemDto dryerDto = new ItemDto(dryer.getId(), dryer.getName(),
            dryer.getDescription(), dryer.getAvailable(), userDtoIrina,
            dryer.getRequest().getId() == null ? null : dryer.getRequest().getId());
    private final ItemDtoInput dryerDtoInput = new ItemDtoInput(dryer.getId(), dryer.getName(),
            dryer.getDescription(), dryer.getAvailable(), dryer.getRequest().getId());
    private final Booking lastBooking = new Booking(6L,
            LocalDateTime.of(2023, 1, 10, 12, 0),
            LocalDateTime.of(2023, 1, 11, 12, 0),
            dryer, userOleg, Status.APPROVED);
    private final BookingIdAndBookerId lastBookingShort = new BookingIdAndBookerId(
            lastBooking.getId(), lastBooking.getBooker().getId());
    private final Booking nextBooking = new Booking(7L,
            LocalDateTime.of(2023, 2, 10, 12, 0),
            LocalDateTime.of(2023, 2, 15, 12, 0),
            dryer, userOleg, Status.APPROVED);
    private final BookingIdAndBookerId nextBookingShort = new BookingIdAndBookerId(
            nextBooking.getId(), nextBooking.getBooker().getId());
    private final ItemDtoBookingAndComments dryerDtoWithBookingsAndComments = new ItemDtoBookingAndComments(
            dryer.getId(), dryer.getName(), dryer.getDescription(),
            dryer.getAvailable(), lastBookingShort, nextBookingShort, new ArrayList<>()
    );
    private final Comment comment = new Comment(8L, "Amazing!", dryer, userOleg,
            LocalDateTime.of(2023, 1, 24, 12, 10));
    private final CommentDto commentDto = new CommentDto(comment.getId(),
            comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;



    @Test
    void create_shouldThrowExceptionIfUserNotExist() {
        Mockito
                .when(mockUserRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.create(99L, dryerDtoInput));
    }

    @Test
    void create_shouldThrowExceptionIfRequestNotExist() {
        Mockito
                .when(mockUserRepository.findById(userOleg.getId()))
                .thenReturn(Optional.of(userOleg));
        Mockito
                .when(mockItemRequestRepository.findById(dryerDto.getRequestId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.create(userOleg.getId(), dryerDtoInput));
    }




    @Test
    void getById_shouldThrowExceptionIfItemNotExist() {
        lenient()
                .when(mockItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.getById(userIrina.getId(), 99L)
        );
    }

    @Test
    void getById_shouldThrowExceptionIfUserNotExist() {
        lenient()
                .when(mockUserRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.getById(99L, userIrina.getId())
        );
    }





    @Test
    void update_shouldThrowExceptionIfUserIsNotOwner() {
        lenient()
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));

        assertThrows(
                NotFoundException.class,
                () -> itemService.update(
                        userOleg.getId(),
                        dryer.getId(),
                        new ItemDto(userIrina.getId(), null, null, null, null, null)
                )
        );
    }

    @Test
    void update_shouldThrowExceptionIfItemNotExist() {
        lenient()
                .when(mockItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.update(
                        userIrina.getId(),
                        99L,
                        new ItemDto(userIrina.getId(), null, null,
                                null, null, null)
                )
        );
    }



    @Test
    void delete_shouldThrowExceptionIfItemNotExist() {
        lenient()
                .when(mockItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.deleteById(userIrina.getId(), 99L));
    }

    @Test
    void delete_shouldThrowExceptionIfUserIsNotOwner() {
        lenient()
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));

        assertThrows(NotFoundException.class,
                () -> itemService.deleteById(userOleg.getId(), dryer.getId()));
    }

    @Test
    void deleteAll_shouldBeSuccess() {
        lenient()
                .when(mockItemRepository.findAll())
                .thenReturn(List.of(dryer));
        itemService.deleteAll();

        Mockito
                .verify(mockItemRepository, Mockito.times(1))
                .deleteAll();
    }


    @Test
    void createComment_shouldThrowExceptionIfItemNotExist() {
        lenient()
                .when(mockItemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(
                        userOleg.getId(),
                        99L,
                        commentDto
                )
        );
    }

    @Test
    void createComment_shouldThrowExceptionIfUserNotExist() {
        lenient()
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));
        Mockito
                .when(mockUserRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(
                        99L,
                        dryer.getId(),
                        commentDto
                )
        );
    }

    @Test
    void createComment_shouldThrowExceptionIfUserIsNotBooker() {
        Mockito
                .when(mockItemRepository.findById(dryer.getId()))
                .thenReturn(Optional.of(dryer));
        Mockito
                .when(mockUserRepository.findById(userOleg.getId()))
                .thenReturn(Optional.of(userOleg));
        Mockito
                .when(mockBookingRepository.findBookingsByBooker_IdAndItem_IdAndEndIsBefore(
                                eq(userOleg.getId()),
                                eq(dryer.getId()),
                                any(LocalDateTime.class)
                        )
                )
                .thenReturn(Collections.emptyList());

        assertThrows(
                CommentAccessException.class,
                () -> itemService.createComment(userOleg.getId(), dryer.getId(), commentDto)
        );
    }
}