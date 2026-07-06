package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void mapToBookingDto_shouldMapAllFields() {
        Booking booking = buildBooking(1L, BookingStatuses.WAITING);

        BookingDto dto = BookingMapper.mapToBookingDto(booking);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatuses.WAITING);
        assertThat(dto.getStart()).isEqualTo(booking.getStartDate());
        assertThat(dto.getEnd()).isEqualTo(booking.getEndDate());
        assertThat(dto.getItem().getId()).isEqualTo(10L);
        assertThat(dto.getBooker().getId()).isEqualTo(2L);
    }

    @Test
    void mapToBooking_shouldCreateBooking_withWaitingStatus() {
        NewBookingDto req = new NewBookingDto();
        req.setItemId(10L);
        req.setStart(LocalDateTime.now().plusHours(1));
        req.setEnd(LocalDateTime.now().plusDays(1));

        User booker = new User(2L, "booker@mail.ru", "Booker");
        Item item = buildItem(10L);

        Booking booking = BookingMapper.mapToBooking(req, booker, item);

        assertThat(booking.getStatus()).isEqualTo(BookingStatuses.WAITING);
        assertThat(booking.getBooker()).isSameAs(booker);
        assertThat(booking.getItem()).isSameAs(item);
        assertThat(booking.getStartDate()).isEqualTo(req.getStart());
        assertThat(booking.getEndDate()).isEqualTo(req.getEnd());
    }

    @Test
    void updateBookingFields_shouldUpdateStartAndEnd_whenBothProvided() {
        Booking booking = buildBooking(1L, BookingStatuses.WAITING);
        LocalDateTime newStart = LocalDateTime.now().plusDays(2);
        LocalDateTime newEnd = LocalDateTime.now().plusDays(3);

        UpdateBookingDto req = new UpdateBookingDto();
        req.setId(1L);
        req.setStart(newStart);
        req.setEnd(newEnd);
        req.setStatus(BookingStatuses.APPROVED);

        Booking updated = BookingMapper.updateBookingFields(booking, req);

        assertThat(updated.getStartDate()).isEqualTo(newStart);
        assertThat(updated.getEndDate()).isEqualTo(newEnd);
        assertThat(updated.getStatus()).isEqualTo(BookingStatuses.APPROVED);
    }

    @Test
    void updateBookingFields_shouldNotUpdateStart_whenStartNull() {
        Booking booking = buildBooking(1L, BookingStatuses.WAITING);
        LocalDateTime originalStart = booking.getStartDate();

        UpdateBookingDto req = new UpdateBookingDto();
        req.setId(1L);
        req.setStatus(BookingStatuses.REJECTED);

        Booking updated = BookingMapper.updateBookingFields(booking, req);

        assertThat(updated.getStartDate()).isEqualTo(originalStart);
        assertThat(updated.getStatus()).isEqualTo(BookingStatuses.REJECTED);
    }

    @Test
    void updateBookingFields_shouldNotUpdateEnd_whenEndNull() {
        Booking booking = buildBooking(1L, BookingStatuses.WAITING);
        LocalDateTime originalEnd = booking.getEndDate();

        UpdateBookingDto req = new UpdateBookingDto();
        req.setId(1L);
        req.setStatus(BookingStatuses.CANCELED);

        Booking updated = BookingMapper.updateBookingFields(booking, req);

        assertThat(updated.getEndDate()).isEqualTo(originalEnd);
    }

    private Booking buildBooking(Long id, BookingStatuses status) {
        User owner = new User(1L, "owner@mail.ru", "Owner");
        User booker = new User(2L, "booker@mail.ru", "Booker");
        Item item = buildItem(10L);
        item.setOwnerUser(owner);

        Booking booking = new Booking();
        booking.setId(id);
        booking.setStatus(status);
        booking.setStartDate(LocalDateTime.now().plusHours(1));
        booking.setEndDate(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    private Item buildItem(Long id) {
        User owner = new User(1L, "owner@mail.ru", "Owner");
        Item item = new Item();
        item.setId(id);
        item.setName("Дрель");
        item.setDescription("Мощная");
        item.setAvailable(true);
        item.setOwnerUser(owner);
        return item;
    }
}
