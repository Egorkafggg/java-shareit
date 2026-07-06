package ru.practicum.shareit.user.dto;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateExistsUserDto {
    Long id;
    String email;
    String name;

    public boolean hasEmail() {
        return !StringUtils.isBlank(this.email);
    }

    public boolean hasName() {
        return !StringUtils.isBlank(this.name);
    }

}
