package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    private BookingMapper() {
        // private constructor to prevent instantiation
    }

    public static BookingDto toDto(Booking booking, User booker, Item item) {
        if (booking == null) {
            return null;
        }
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        if (booker != null) {
            dto.setBooker(UserMapper.toDto(booker));
        }
        if (item != null) {
            dto.setItem(ItemMapper.toDto(item));
        }

        return dto;
    }

    public static Booking toEntity(BookingRequestDto requestDto, Long bookerId) {
        if (requestDto == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setStart(requestDto.getStart());
        booking.setEnd(requestDto.getEnd());
        booking.setBookerId(bookerId);
        booking.setItemId(requestDto.getItemId());
        return booking;
    }
}