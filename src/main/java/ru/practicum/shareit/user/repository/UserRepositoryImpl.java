package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long countId = 1;

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        emailChecking(user.getEmail());
        user.setId(countId++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }


    @Override
    public User update(UserDto userDto, User user) {
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!user.getEmail().equals(userDto.getEmail())) {
                if (emails.contains(userDto.getEmail())) {
                    throw new ServerException("This email has been registered");
                }
                emails.remove(user.getEmail());
                emails.add(userDto.getEmail());
            }
            user.setEmail(userDto.getEmail());
            users.replace(user.getId(), user);
        }
        return user;
    }

    @Override
    public void deleteById(long id) {
        if (users.containsKey(id)) {
            emails.remove(users.get(id).getEmail());
            users.remove(id);
        } else {
            throw new NotFoundException("User with id is not found");
        }
    }

    @Override
    public void deleteAll() {
        emails.clear();
        users.clear();
    }

    public Set<String> getEmails() {
        return emails;
    }

    private void emailChecking(String user) {
        if (emails.contains(user)) {
            throw new ServerException("This email already has been registered");
        }
    }
}
