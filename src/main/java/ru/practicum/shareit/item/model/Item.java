package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class Item {
    Long id;
    @NotBlank(message = "Наименование предмета бронирования не может быть пустым")
    String name;
    @NotBlank(message = "Описание предмета бронирования не должно быть пустым")
    String description;
    @NotNull(message = "Нужно указать статус предмета бронирования: он занят или свободен")
    Boolean available;
    Long owner;
    Long request;
}
