package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OtherOwnerItemEditingException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongBookingStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void create_shouldThrowNotFoundException_whenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        NewBookingDto dto = buildNewDto(1L);
        assertThatThrownBy(() -> bookingService.create(2L, dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowNotFoundException_whenUserNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(buildItem(1L, 99L, true)));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(2L, buildNewDto(1L)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowValidationException_whenItemNotAvailable() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(buildItem(1L, 99L, false)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(buildUser(2L)));

        assertThatThrownBy(() -> bookingService.create(2L, buildNewDto(1L)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("не доступна");
    }

    @Test
    void create_shouldThrowValidationException_whenUserIsOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(buildItem(1L, 2L, true)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(buildUser(2L)));

        assertThatThrownBy(() -> bookingService.create(2L, buildNewDto(1L)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("собственную вещь");
    }

    @Test
    void create_shouldSaveAndReturn_whenValid() {
        Item item = buildItem(1L, 99L, true);
        User booker = buildUser(2L);
        Booking saved = buildBooking(10L, item, booker, BookingStatuses.WAITING);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any())).thenReturn(saved);

        BookingDto result = bookingService.create(2L, buildNewDto(1L));

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(BookingStatuses.WAITING);
        verify(bookingRepository).save(any());
    }

    @Test
    void findBooking_shouldThrowNotFoundException_whenBookingMissing() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findBooking(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findBooking_shouldThrowValidationException_whenCalledByStranger() {
        User owner = buildUser(1L);
        User booker = buildUser(2L);
        Item item = buildItem(5L, 1L, true);
        item.setOwnerUser(owner);
        Booking booking = buildBooking(10L, item, booker, BookingStatuses.WAITING);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> bookingService.findBooking(10L, 99L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void findBooking_shouldReturn_whenCalledByBooker() {
        User owner = buildUser(1L);
        User booker = buildUser(2L);
        Item item = buildItem(5L, 1L, true);
        item.setOwnerUser(owner);
        Booking booking = buildBooking(10L, item, booker, BookingStatuses.WAITING);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        BookingDto result = bookingService.findBooking(10L, 2L);
        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void findBooking_shouldReturn_whenCalledByOwner() {
        User owner = buildUser(1L);
        User booker = buildUser(2L);
        Item item = buildItem(5L, 1L, true);
        item.setOwnerUser(owner);
        Booking booking = buildBooking(10L, item, booker, BookingStatuses.WAITING);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        BookingDto result = bookingService.findBooking(10L, 1L);
        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void findAllBookingsByUser_ALL_shouldReturnAllBookings() {
        User user = buildUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(1L)).thenReturn(List.of());

        Collection<BookingDto> result = bookingService.findAllBookingsByUser(1L, "ALL");
        assertThat(result).isEmpty();
    }

    @Test
    void findAllBookingsByUser_CURRENT_shouldDelegateToCurrentQuery() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllCurrentBookingByBookerId(1L)).thenReturn(List.of());

        Collection<BookingDto> result = bookingService.findAllBookingsByUser(1L, "CURRENT");
        assertThat(result).isEmpty();
        verify(bookingRepository).findAllCurrentBookingByBookerId(1L);
    }

    @Test
    void findAllBookingsByUser_PAST_shouldDelegateToPastQuery() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllPastBookingByBookerId(1L)).thenReturn(List.of());

        bookingService.findAllBookingsByUser(1L, "PAST");
        verify(bookingRepository).findAllPastBookingByBookerId(1L);
    }

    @Test
    void findAllBookingsByUser_FUTURE_shouldDelegateToFutureQuery() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllFutureBookingByBookerId(1L)).thenReturn(List.of());

        bookingService.findAllBookingsByUser(1L, "FUTURE");
        verify(bookingRepository).findAllFutureBookingByBookerId(1L);
    }

    @Test
    void findAllBookingsByUser_WAITING_shouldFilterByWaitingStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllByBookerIdAndStatus(1L, BookingStatuses.WAITING)).thenReturn(List.of());

        bookingService.findAllBookingsByUser(1L, "WAITING");
        verify(bookingRepository).findAllByBookerIdAndStatus(1L, BookingStatuses.WAITING);
    }

    @Test
    void findAllBookingsByUser_REJECTED_shouldFilterByRejectedStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllByBookerIdAndStatus(1L, BookingStatuses.REJECTED)).thenReturn(List.of());

        bookingService.findAllBookingsByUser(1L, "REJECTED");
        verify(bookingRepository).findAllByBookerIdAndStatus(1L, BookingStatuses.REJECTED);
    }

    @Test
    void findAllBookingsByUser_shouldThrowNotFoundException_whenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findAllBookingsByUser(99L, "ALL"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllBookingsByOwnerItems_ALL_shouldDelegateToOwnerQuery() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllByOwnerId(1L)).thenReturn(List.of());

        bookingService.findAllBookingsByOwnerItems(1L, "ALL");
        verify(bookingRepository).findAllByOwnerId(1L);
    }

    @Test
    void findAllBookingsByOwnerItems_CURRENT_shouldDelegateToCurrentOwnerQuery() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllCurrentBookingByOwnerId(1L)).thenReturn(List.of());

        bookingService.findAllBookingsByOwnerItems(1L, "CURRENT");
        verify(bookingRepository).findAllCurrentBookingByOwnerId(1L);
    }

    @Test
    void findAllBookingsByOwnerItems_PAST_shouldDelegateToPastOwnerQuery() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllPastBookingByOwnerId(1L)).thenReturn(List.of());

        bookingService.findAllBookingsByOwnerItems(1L, "PAST");
        verify(bookingRepository).findAllPastBookingByOwnerId(1L);
    }

    @Test
    void findAllBookingsByOwnerItems_FUTURE_shouldDelegateToFutureOwnerQuery() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllFutureBookingByOwnerId(1L)).thenReturn(List.of());

        bookingService.findAllBookingsByOwnerItems(1L, "FUTURE");
        verify(bookingRepository).findAllFutureBookingByOwnerId(1L);
    }

    @Test
    void findAllBookingsByOwnerItems_WAITING_shouldFilterByWaitingStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllByOwnerIdAndStatus(1L, BookingStatuses.WAITING)).thenReturn(List.of());

        bookingService.findAllBookingsByOwnerItems(1L, "WAITING");
        verify(bookingRepository).findAllByOwnerIdAndStatus(1L, BookingStatuses.WAITING);
    }

    @Test
    void findAllBookingsByOwnerItems_REJECTED_shouldFilterByRejectedStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(bookingRepository.findAllByOwnerIdAndStatus(1L, BookingStatuses.REJECTED)).thenReturn(List.of());

        bookingService.findAllBookingsByOwnerItems(1L, "REJECTED");
        verify(bookingRepository).findAllByOwnerIdAndStatus(1L, BookingStatuses.REJECTED);
    }

    @Test
    void update_shouldSave_whenBothDatesProvided() {
        Item item = buildItem(1L, 99L, true);
        User booker = buildUser(2L);
        Booking existing = buildBooking(1L, item, booker, BookingStatuses.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookingRepository.save(any())).thenReturn(existing);

        UpdateBookingDto dto = new UpdateBookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setStatus(BookingStatuses.APPROVED);

        BookingDto result = bookingService.update(dto);
        assertThat(result).isNotNull();
        verify(bookingRepository).save(any());
    }

    @Test
    void update_shouldSave_whenNoDatesProvided() {
        Item item = buildItem(1L, 99L, true);
        User booker = buildUser(2L);
        Booking existing = buildBooking(1L, item, booker, BookingStatuses.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookingRepository.save(any())).thenReturn(existing);

        UpdateBookingDto dto = new UpdateBookingDto();
        dto.setId(1L);
        dto.setStatus(BookingStatuses.REJECTED);

        bookingService.update(dto);
        verify(bookingRepository).save(any());
    }

    @Test
    void delete_shouldThrowNotFoundException_whenBookingMissing() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.delete(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_shouldCallRepositoryDelete() {
        Item item = buildItem(1L, 99L, true);
        User booker = buildUser(2L);
        Booking booking = buildBooking(1L, item, booker, BookingStatuses.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.delete(1L);
        verify(bookingRepository).delete(booking);
    }

    @Test
    void approveBooking_shouldThrowNotFoundException_whenBookingMissing() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.approveBooking(1L, 1L, true))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void approveBooking_shouldThrowOtherOwnerException_whenNotItemOwner() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, 1L, true);
        item.setOwnerUser(owner);
        Booking booking = buildBooking(10L, item, buildUser(2L), BookingStatuses.WAITING);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.approveBooking(10L, 99L, true))
                .isInstanceOf(OtherOwnerItemEditingException.class);
    }

    @Test
    void approveBooking_shouldThrowWrongStatusException_whenNotWaiting() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, 1L, true);
        item.setOwnerUser(owner);
        Booking booking = buildBooking(10L, item, buildUser(2L), BookingStatuses.APPROVED);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.approveBooking(10L, 1L, true))
                .isInstanceOf(WrongBookingStatusException.class);
    }

    @Test
    void approveBooking_shouldSetApproved_whenOwnerApprovesWaitingBooking() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, 1L, true);
        item.setOwnerUser(owner);
        Booking booking = buildBooking(10L, item, buildUser(2L), BookingStatuses.WAITING);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));

        BookingDto result = bookingService.approveBooking(10L, 1L, true);
        assertThat(result.getStatus()).isEqualTo(BookingStatuses.APPROVED);
    }

    @Test
    void approveBooking_shouldSetRejected_whenOwnerRejectsWaitingBooking() {
        User owner = buildUser(1L);
        Item item = buildItem(5L, 1L, true);
        item.setOwnerUser(owner);
        Booking booking = buildBooking(10L, item, buildUser(2L), BookingStatuses.WAITING);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));

        BookingDto result = bookingService.approveBooking(10L, 1L, false);
        assertThat(result.getStatus()).isEqualTo(BookingStatuses.REJECTED);
    }

    private User buildUser(Long id) {
        return new User(id, "user" + id + "@mail.ru", "User" + id);
    }

    private Item buildItem(Long id, Long ownerId, boolean available) {
        User owner = buildUser(ownerId);
        Item item = new Item();
        item.setId(id);
        item.setName("Item" + id);
        item.setDescription("Desc" + id);
        item.setAvailable(available);
        item.setOwnerUser(owner);
        return item;
    }

    private Booking buildBooking(Long id, Item item, User booker, BookingStatuses status) {
        Booking b = new Booking();
        b.setId(id);
        b.setItem(item);
        b.setBooker(booker);
        b.setStartDate(LocalDateTime.now().plusHours(1));
        b.setEndDate(LocalDateTime.now().plusDays(1));
        b.setStatus(status);
        return b;
    }

    private NewBookingDto buildNewDto(Long itemId) {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusHours(1));
        dto.setEnd(LocalDateTime.now().plusDays(1));
        return dto;
    }
}
