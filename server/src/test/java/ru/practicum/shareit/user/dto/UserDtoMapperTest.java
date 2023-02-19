package ru.practicum.shareit.user.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;


class UserDtoMapperTest {

    @Test
    void toDto() {
        UserMapper userMapper = new UserMapperImpl();
        User user = new User(2L, "Man", "van@yandex.ru");
        UserDto userDto = userMapper.toDto(user);

        Assertions.assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "Man")
                .hasFieldOrPropertyWithValue("email", "van@yandex.ru");
    }

    @Test
    void fromDto() {
        UserMapper userMapper = new UserMapperImpl();
        UserDto userDto = new UserDto(2L, "Man", "van@yandex.ru");
        User user = userMapper.fromDto(userDto);

        Assertions.assertThat(user)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "Man")
                .hasFieldOrPropertyWithValue("email", "van@yandex.ru");
    }
}