package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

public interface UserRepository {
    Optional<User> findById(long id);

    List<User> findAll();

    User save(User user);

    User update(UserDto userDto, User user);

    void deleteById(long id);

    void deleteAll();

    Set<String> getEmails();
}
