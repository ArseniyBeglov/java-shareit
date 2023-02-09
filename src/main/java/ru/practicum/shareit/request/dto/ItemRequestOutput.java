package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoRequests;
import ru.practicum.shareit.user.dto.UserDtoIdAndName;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestOutput {
    private Long id;
    private String description;
    private UserDtoIdAndName requestor;
    private LocalDateTime created;
    private List<ItemDtoRequests> items;
}