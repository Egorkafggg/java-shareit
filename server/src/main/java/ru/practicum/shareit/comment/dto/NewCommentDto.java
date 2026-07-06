package ru.practicum.shareit.comment.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {

    String text;

    Long itemId;

    Long authorId;
}