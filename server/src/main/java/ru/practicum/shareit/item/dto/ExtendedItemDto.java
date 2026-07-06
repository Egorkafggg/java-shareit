package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.shareit.comment.dto.CommentDto;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExtendedItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    String name;
    String description;
    Boolean available;
    LocalDateTime lastBooking;
    LocalDateTime nextBooking;
    List<CommentDto> comments;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long ownerId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long requestId;
}