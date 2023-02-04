package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;


@Data
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private User requestor;
}
