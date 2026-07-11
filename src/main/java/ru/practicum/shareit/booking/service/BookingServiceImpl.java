package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingRequestDto requestDto) {
        Optional<User> bookerOpt = userRepository.findById(userId);
        if (bookerOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User booker = bookerOpt.get();

        Optional<Item> itemOpt = itemRepository.findById(requestDto.getItemId());
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemOpt.get();

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }

        if (item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Owner cannot book their own item");
        }

        if (requestDto.getEnd().isBefore(requestDto.getStart()) ||
                requestDto.getEnd().equals(requestDto.getStart())) {
            throw new BadRequestException("End date must be after start date");
        }

        Booking booking = BookingMapper.toEntity(requestDto, userId);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        return BookingMapper.toDto(booking, booker, item);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        // Проверяем, что пользователь существует
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        // Проверяем, что пользователь - владелец вещи
        if (!item.getOwnerId().equals(userId)) {
            throw new ForbiddenException("Only owner can approve booking");  // ← 403
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Booking status is not WAITING");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);

        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new NotFoundException("Booker not found"));

        return BookingMapper.toDto(booking, booker, item);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("Booking not found");
        }
        Booking booking = bookingOpt.get();

        Optional<Item> itemOpt = itemRepository.findById(booking.getItemId());
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemOpt.get();

        if (!booking.getBookerId().equals(userId) && !item.getOwnerId().equals(userId)) {
            throw new ForbiddenException("User is not booker or owner");
        }

        Optional<User> bookerOpt = userRepository.findById(booking.getBookerId());
        if (bookerOpt.isEmpty()) {
            throw new NotFoundException("Booker not found");
        }
        User booker = bookerOpt.get();

        return BookingMapper.toDto(booking, booker, item);
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, BookingState state) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
                .map(b -> {
                    Optional<User> bookerOpt = userRepository.findById(b.getBookerId());
                    if (bookerOpt.isEmpty()) {
                        throw new NotFoundException("Booker not found");
                    }
                    User booker = bookerOpt.get();

                    Optional<Item> itemOpt = itemRepository.findById(b.getItemId());
                    if (itemOpt.isEmpty()) {
                        throw new NotFoundException("Item not found");
                    }
                    Item item = itemOpt.get();

                    return BookingMapper.toDto(b, booker, item);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, BookingState state) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        if (itemRepository.findByOwnerIdOrderByIdAsc(userId).isEmpty()) {
            throw new ValidationException("User has no items");
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingsByOwnerId(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwnerId(userId, now);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByOwnerId(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByOwnerId(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByOwnerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByOwnerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findBookingsByOwnerId(userId);
        }

        return bookings.stream()
                .map(b -> {
                    Optional<User> bookerOpt = userRepository.findById(b.getBookerId());
                    if (bookerOpt.isEmpty()) {
                        throw new NotFoundException("Booker not found");
                    }
                    User booker = bookerOpt.get();

                    Optional<Item> itemOpt = itemRepository.findById(b.getItemId());
                    if (itemOpt.isEmpty()) {
                        throw new NotFoundException("Item not found");
                    }
                    Item item = itemOpt.get();

                    return BookingMapper.toDto(b, booker, item);
                })
                .collect(Collectors.toList());
    }
}