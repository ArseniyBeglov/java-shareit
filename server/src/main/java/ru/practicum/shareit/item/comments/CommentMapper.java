package ru.practicum.shareit.item.comments;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;


public interface CommentMapper {
    CommentDto toDto(Comment comment);

    Comment fromDto(CommentDto commentDto, Item item, User user);
}
