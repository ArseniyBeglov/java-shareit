package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoRequests;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutput;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoIdAndName;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private final User userOleg = new User(1L, "Oleg", "oleg@yandex.ru");
    private final User userIrina = new User(2L, "Irina", "irina@yandex.ru");
    private final UserDto userDtoIrina = new UserDto(userIrina.getId(), userIrina.getName(), userIrina.getEmail());
    private final UserDtoIdAndName userDtoShortIrina = new UserDtoIdAndName(userIrina.getId(), userIrina.getName());
    private final ItemRequest request = new ItemRequest(
            4L,
            "I want it!",
            userIrina,
            LocalDateTime.of(2023, 1, 20, 12, 0, 0)
    );
    private final ItemRequestDto itemRequestDtoInput = new ItemRequestDto(
            request.getId(),
            request.getDescription(),
            userDtoIrina.getId(),
            request.getCreated()
    );
    private final Item dryer = new Item(
            3L,
            "Dryer",
            "For curly hair",
            true,
            userIrina,
            request);
    private final ItemDtoRequests itemDtoForRequest = new ItemDtoRequests(
            dryer.getId(),
            dryer.getName(),
            dryer.getDescription(),
            dryer.getAvailable(),
            dryer.getRequest().getId()
    );
    private final ItemRequestOutput requestDtoOutput = new ItemRequestOutput(
            request.getId(),
            request.getDescription(),
            userDtoShortIrina,
            request.getCreated(),
            List.of(itemDtoForRequest)
    );

    private final ItemRequestOutput requestDtoOutputWithoutItems = new ItemRequestOutput(
            request.getId(),
            request.getDescription(),
            userDtoShortIrina,
            request.getCreated(),
            null
    );
    @Mock
    private ItemRequestRepository mockRequestRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void create_shouldSuccess() {
        lenient()
                .when(mockUserRepository.findById(userIrina.getId()))
                .thenReturn(Optional.of(userIrina));
        lenient()
                .when(mockUserRepository.existsById(userIrina.getId()))
                .thenReturn(true);
        Mockito
                .when(mockRequestRepository.save(any()))
                .thenReturn(request);
        ItemRequestOutput actual = itemRequestService.create(userIrina.getId(), itemRequestDtoInput);

        assertEquals(requestDtoOutputWithoutItems, actual);
    }

    @Test
    void create_shouldThrowExceptionIfWrongUserId() {
        Mockito
                .when(mockUserRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(99L, itemRequestDtoInput));
    }

    @Test
    void getAll_shouldSuccess() {
        Mockito
                .when(mockUserRepository.existsById(userOleg.getId()))
                .thenReturn(true);
        Mockito
                .when(mockRequestRepository.findByRequestor_Id(userOleg.getId()))
                .thenReturn(List.of(request));
        Mockito
                .when(mockItemRepository.findByRequest_IdIn(anyList()))
                .thenReturn(List.of(dryer));
        List<ItemRequestOutput> actual = itemRequestService.getAll(userOleg.getId());

        assertEquals(List.of(requestDtoOutput), actual);
    }

    @Test
    void getAll_shouldThrowExceptionIfWrongUserId() {
        Mockito
                .when(mockUserRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAll(99L));
    }

    @Test
    void getAllAnotherUsers_shouldSuccess() {
        Mockito
                .when(mockRequestRepository.findByRequestor_IdNot(
                                userIrina.getId(),
                                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "created"))
                        )
                )
                .thenReturn(List.of(request));
        Mockito
                .when(mockItemRepository.findByRequest_IdIn(anyList()))
                .thenReturn(List.of(dryer));
        List<ItemRequestOutput> actual = itemRequestService.getAllAnotherUsers(userIrina.getId(), 0, 1);

        assertThat(actual).isNotEmpty();
        assertEquals(List.of(requestDtoOutput), actual);
    }

    @Test
    void getById_shouldSuccess() {
        Mockito
                .when(mockUserRepository.existsById(userOleg.getId()))
                .thenReturn(true);
        Mockito
                .when(mockRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(mockRequestRepository.existsById(request.getId()))
                .thenReturn(true);
        Mockito
                .when(mockItemRepository.findByRequest_IdOrderById(request.getId()))
                .thenReturn(List.of(dryer));
        ItemRequestOutput actual = itemRequestService.getById(userOleg.getId(), request.getId());

        assertEquals(requestDtoOutput, actual);
    }

    @Test
    void getById_shouldThrowExceptionIfWrongUserId() {
        Mockito
                .when(mockUserRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getById(99L, request.getId())
        );
    }

    @Test
    void getById_shouldThrowExceptionIfWrongRequestId() {
        Mockito
                .when(mockUserRepository.existsById(userOleg.getId()))
                .thenReturn(true);
        Mockito
                .when(mockRequestRepository.existsById(99L))
                .thenReturn(false);
        assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getById(userOleg.getId(), 99L)
        );
    }
}