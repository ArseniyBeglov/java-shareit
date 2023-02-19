package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;


public class ItemRequestClient extends BaseClient {

    @Autowired
    public ItemRequestClient(RestTemplate builder) {
        super(builder);
    }

    public ResponseEntity<Object> getItemRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemRequest(long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getAllAnotherUsers(long requestorId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", requestorId, parameters);
    }

    public ResponseEntity<Object> create(long requestorId, ItemRequestDto itemRequestDto) {
        return post("", requestorId, itemRequestDto);
    }
}
