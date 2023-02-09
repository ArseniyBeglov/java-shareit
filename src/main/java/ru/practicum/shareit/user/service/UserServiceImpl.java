package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll() {
        log.debug("Request GET to /users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long id) {
        log.debug("Request GET to /users/{}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " is not found"));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.debug("Request POST to /users, with id = {}, name = {}, email = {}",
                userDto.getId(), userDto.getName(), userDto.getEmail());
        User user = userMapper.fromDto(userDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto update(long id, UserDto userDto) {
        log.debug("Request PATCH to /users, with id = {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("User with id = " + id + " not found"));
        userDto.setId(id);
        return userMapper.toDto(update(userDto, user));
    }

    @Override
    public void deleteById(long id) {
        log.debug("Request DELETE to /users/{}", id);
        userRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.debug("Request DELETE to /users)");
        userRepository.deleteAll();
    }

    private User update(UserDto userDto, User user) {
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }
}