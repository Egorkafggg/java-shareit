package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingRequestDto requestDto);

    BookingDto approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsByUser(Long userId, BookingState state);

    List<BookingDto> getBookingsByOwner(Long userId, BookingState state);
}
