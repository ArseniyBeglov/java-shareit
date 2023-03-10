package ru.practicum.shareit.item.comments;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;


import java.time.LocalDateTime;

@Component
public class CommentMapperImpl implements CommentMapper {
    @Override
    public CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(),
                comment.getAuthor().getName(), comment.getCreated());
    }

    @Override
    public Comment fromDto(CommentDto commentDto, Item item, User user) {
        return new Comment(commentDto.getId(), commentDto.getText(),
                item, user, LocalDateTime.now());
    }
}
