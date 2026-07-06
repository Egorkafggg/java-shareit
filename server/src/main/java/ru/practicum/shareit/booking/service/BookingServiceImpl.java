package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.model.BookingStates;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    BookingRepository repository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository repository, UserRepository userRepository, ItemRepository itemRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    private Booking findById(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование c ID %d не найдено", bookingId)));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь создающий бронирование " +
                        "c ID %d не найден", userId)));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь для бронирования c ID %d не найдена", itemId)));
    }

    @Override
    @Transactional
    public BookingDto create(Long userId, NewBookingDto request) {
        log.debug("Создаем запись о бронировании");

        Item findItem = findItemById(request.getItemId());
        User findUser = findUserById(userId);

        if (!findItem.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования!");
        }

        if (findUser.getId().equals(findItem.getOwnerUser().getId())) {
            throw new ValidationException("Нельзя бронировать собственную вещь");
        }

        Booking booking = BookingMapper.mapToBooking(request, findUser, findItem);
        booking = repository.save(booking);

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto findBooking(Long bookingId, Long userId) {
        log.debug("Ищем бронирование с ID {}", bookingId);

        Booking booking = findById(bookingId);
        User owner = findUserById(booking.getItem().getOwnerUser().getId());
        if (!booking.getBooker().getId().equals(userId) && !owner.getId().equals(userId)) {
            throw new ValidationException("Только владелец вещи и создатель брони могут просматривать данные о бронировании");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> findAllBookingsByUser(Long userId, String state) {
        BookingStates currentState = BookingStates.valueOf(state);
        User findUser = findUserById(userId);
        Collection<Booking> bookingList;

        switch (currentState) {
            case ALL:
                bookingList = repository.findAllByBookerId(userId);
                log.debug("Получаем записи о всех бронированиях пользователя");
                break;
            case CURRENT:
                bookingList = repository.findAllCurrentBookingByBookerId(userId);
                log.debug("Получаем записи о всех текущих бронированиях пользователя");
                break;
            case PAST:
                bookingList = repository.findAllPastBookingByBookerId(userId);
                log.debug("Получаем записи о завершенных бронированиях пользователя");
                break;
            case FUTURE:
                bookingList = repository.findAllFutureBookingByBookerId(userId);
                log.debug("Получаем записи о будущих бронированиях пользователя");
                break;
            case WAITING:
                bookingList = repository.findAllByBookerIdAndStatus(userId, BookingStatuses.WAITING);
                log.debug("Получаем записи бронирований ожидающих подтверждения пользователя");
                break;
            case REJECTED:
                bookingList = repository.findAllByBookerIdAndStatus(userId, BookingStatuses.REJECTED);
                log.debug("Получаем записи об отклоненных бронированиях пользователя");
                break;
            default:
                throw new IllegalArgumentException("Не верно указан параметр state");
        }

        return bookingList.stream()
                .map(BookingMapper::mapToBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> findAllBookingsByOwnerItems(Long userId, String state) {
        BookingStates currentState = BookingStates.valueOf(state);
        User findUser = findUserById(userId);
        Collection<Booking> bookingList;

        switch (currentState) {
            case ALL:
                bookingList = repository.findAllByOwnerId(userId);
                log.debug("Получаем записи бронирований вещей пользователя");
                break;
            case CURRENT:
                bookingList = repository.findAllCurrentBookingByOwnerId(userId);
                log.debug("Получаем записи о всех текущих бронированиях вещей пользователя");
                break;
            case PAST:
                bookingList = repository.findAllPastBookingByOwnerId(userId);
                log.debug("Получаем записи о завершенных бронированиях вещей пользователя");
                break;
            case FUTURE:
                bookingList = repository.findAllFutureBookingByOwnerId(userId);
                log.debug("Получаем записи о будущих бронированиях вещей пользователя");
                break;
            case WAITING:
                bookingList = repository.findAllByOwnerIdAndStatus(userId, BookingStatuses.WAITING);
                log.debug("Получаем записи о бронировании вещей пользователя ожидающих подтверждения");
                break;
            case REJECTED:
                bookingList = repository.findAllByOwnerIdAndStatus(userId, BookingStatuses.REJECTED);
                log.debug("Получаем записи об отклоненных бронированиях вещей пользователя");
                break;
            default:
                throw new IllegalArgumentException("Не верно указан параметр state");
        }

        return bookingList.stream()
                .map(BookingMapper::mapToBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto update(UpdateBookingDto request) {
        log.debug("Обновляем данные о бронировании");

        Booking updatedItem = BookingMapper.updateBookingFields(findById(request.getId()), request);
        updatedItem = repository.save(updatedItem);

        return BookingMapper.mapToBookingDto(updatedItem);
    }

    @Override
    @Transactional
    public void delete(Long bookingId) {
        Booking booking = findById(bookingId);
        log.debug("Удаляем данные о бронировании с ID {}", booking.getId());
        repository.delete(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = findById(bookingId);
        Item item = findItemById(booking.getItem().getId());

        if (!item.getOwnerUser().getId().equals(userId)) {
            throw new OtherOwnerItemEditingException("Менять статус вещи может только её владелец");
        }

        if (!booking.getStatus().equals(BookingStatuses.WAITING)) {
            throw new WrongBookingStatusException("Вещь уже занята!");
        }

        booking.setStatus(approved ? BookingStatuses.APPROVED : BookingStatuses.REJECTED);
        return BookingMapper.mapToBookingDto(booking);
    }
}

