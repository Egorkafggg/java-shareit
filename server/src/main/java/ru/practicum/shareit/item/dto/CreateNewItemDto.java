package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class CreateNewItemDto {

    Long id;

    String name;

    String description;

    Boolean available;

    Long owner;

    Long requestId;

    public boolean hasRequestId() {
        return requestId != null;
    }
}