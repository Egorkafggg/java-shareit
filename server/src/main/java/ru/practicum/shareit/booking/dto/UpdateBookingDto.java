package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import ru.practicum.shareit.booking.model.BookingStatuses;

@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    BookingStatuses status;
    Long bookerId; // ссылка на пользователя

    public boolean hasStart() {
        return this.start != null;
    }

    public boolean hasEnd() {
        return this.end != null;
    }
}

