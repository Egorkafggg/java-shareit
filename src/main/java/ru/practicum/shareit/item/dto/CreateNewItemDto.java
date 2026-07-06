package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class CreateNewItemDto {
    Long id;
    @NotBlank(message = "Наименование предмета бронирования не должно быть пустым")
    String name;
    @NotBlank(message = "Описание предмета бронирования не должно быть пустым")
    String description;
    @NotNull(message = "Нужно указать состояние предмета бронирования: он доступен или занят?")
    Boolean available;
    Long owner;
    Long request;
}