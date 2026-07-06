package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;

import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String id = "/{booking-id}";
    private final String owner = "/owner";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String BOOKING_ID = "booking-id";

    @GetMapping(id)
    public BookingDto findBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @PathVariable(BOOKING_ID) Long bookingId) {
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> findAllBookingsByUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByUser(userId, state);
    }

    @GetMapping(owner)
    public Collection<BookingDto> findAllBookingsByOwnerItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                                              @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByOwnerItems(userId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                             @RequestBody NewBookingDto booking) {
        return bookingService.create(userId, booking);
    }

    @PutMapping(id)
    public BookingDto update(@RequestBody UpdateBookingDto newBooking) {
        return bookingService.update(newBooking);
    }

    @DeleteMapping(id)
    public void delete(@PathVariable(BOOKING_ID) Long bookingId) {
        bookingService.delete(bookingId);
    }

    @PatchMapping(id)
    public BookingDto approveBooking(@PathVariable(BOOKING_ID) Long bookingId,
                                     @RequestHeader(USER_ID_HEADER) Long userId,
                                     @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }
}
