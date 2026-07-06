package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class ItemRequest {
    Long id;
    @NotBlank(message = "Текст запроса предмета бронирования не должен быть пустым")
    String description;
    @NotNull(message = "Нужно указать пользователя, оставившего заявку на бронь")
    User requestor;
    @NotNull(message = "Дата/время создания заявки на бронь не должна быть пустой")
    LocalDateTime created;
}
