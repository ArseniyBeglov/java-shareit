package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;


public class UserClient extends BaseClient {

    @Autowired
    public UserClient(RestTemplate builder) {
        super(builder);
    }

    public ResponseEntity<Object> getUsers() {
        return get("");
    }

    public ResponseEntity<Object> getUser(long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> patchUser(long id, UserDto userDto) {
        return patch("/" + id, userDto);
    }

    public ResponseEntity<Object> deleteUser(long id) {

        return delete("/" + id);
    }
}
