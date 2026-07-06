package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import java.util.Collection;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;

public interface BookingService {
    BookingDto create(Long userId, NewBookingDto request);

    BookingDto findBooking(Long bookingId, Long userId);

    Collection<BookingDto> findAllBookingsByUser(Long userId, String state);

    Collection<BookingDto> findAllBookingsByOwnerItems(Long userId, String state);

    BookingDto update(UpdateBookingDto request);

    void delete(Long bookingId);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);
}
