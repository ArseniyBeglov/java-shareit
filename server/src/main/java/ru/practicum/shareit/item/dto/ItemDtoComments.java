package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.comments.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoComments {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
}
