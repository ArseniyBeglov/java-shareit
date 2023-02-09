package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final User userOleg = new User(1L, "Oleg", "oleg@yandex.ru");
    private final UserDto userDtoOleg = new UserDto(userOleg.getId(), userOleg.getName(), userOleg.getEmail());
    @Mock
    private UserRepository mockUserRepository;
    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void getById_shouldThrowExceptionIfWrongUserId() {
        Mockito
                .when(mockUserRepository.findById(128L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(128L));
    }


    @Test
    void update_shouldThrowExceptionIfWrongUserId() {
        UserDto userDtoIrina = new UserDto(null, "Irina", "irinajunior@yanderx.ru");
        Mockito
                .when(mockUserRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(99L, userDtoIrina));
    }

    @Test
    void deleteById_shouldBeSuccess() {
        User userOleg = new User(2L, "Oleg", "oleg@yandex.ru");
        userService.deleteById(userOleg.getId());
        Mockito
                .verify(mockUserRepository, Mockito.times(1)).deleteById(userOleg.getId());
    }

    @Test
    void deleteAll_shouldBeSuccess() {
        userService.deleteAll();
        Mockito
                .verify(mockUserRepository, Mockito.times(1)).deleteAll();
    }
}