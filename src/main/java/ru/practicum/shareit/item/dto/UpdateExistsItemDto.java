package ru.practicum.shareit.item.dto;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class UpdateExistsItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    Long request;

    public boolean hasName() {
        return !StringUtils.isBlank(this.name);
    }

    public boolean hasDescription() {
        return !StringUtils.isBlank(this.description);
    }

    public boolean hasAvailable() {
        return this.available != null;
    }

}
