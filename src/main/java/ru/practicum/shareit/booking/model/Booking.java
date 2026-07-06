package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
public class Booking {
    Long id;
    @NotNull(message = "Дата начала бронирования - обязательный реквизит")
    LocalDateTime start;
    @NotNull(message = "Дата окончания бронирования - обязательный реквизит")
    LocalDateTime end;
    @NotNull(message = "Бронируемая вещь/предмет - обязательный реквизит")
    Item item;
    @NotNull(message = "Заказчик бронирования - обязательный реквизит")
    User booker;
    @NotNull(message = "Статус бронирования должен быть одним из: " +
            "\"WAITING\", \"APPROVED\", \"REJECTED\", \"CANCELED\".")
    BookingStatuses status;
}
