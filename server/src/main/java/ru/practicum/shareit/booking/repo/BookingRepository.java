package ru.practicum.shareit.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Для пользователя (booker)
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    // Для владельца вещи (owner) - используем @Query
    @Query("SELECT b FROM Booking b WHERE b.itemId IN (SELECT i.id FROM Item i WHERE i.ownerId = :ownerId) ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN (SELECT i.id FROM Item i WHERE i.ownerId = :ownerId) " +
            "AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN (SELECT i.id FROM Item i WHERE i.ownerId = :ownerId) " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN (SELECT i.id FROM Item i WHERE i.ownerId = :ownerId) " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.itemId IN (SELECT i.id FROM Item i WHERE i.ownerId = :ownerId) " +
            "AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status);

    // Для конкретной вещи
    List<Booking> findByItemIdAndStatusOrderByStartAsc(Long itemId, BookingStatus status);

    // Для проверки, что пользователь брал вещь
    List<Booking> findByItemIdAndBookerIdAndStatusAndEndBefore(
            Long itemId, Long bookerId, BookingStatus status, LocalDateTime now);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
            Long itemId, Long bookerId, BookingStatus status, LocalDateTime now);
}