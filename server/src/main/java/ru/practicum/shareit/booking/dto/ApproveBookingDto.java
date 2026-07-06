package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import ru.practicum.shareit.booking.model.BookingStatuses;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApproveBookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemDto item;
    BookingStatuses status;
    UserDto booker;
}

