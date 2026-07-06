package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatuses bookingStatus);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = :id " +
            "and CURRENT_TIMESTAMP BETWEEN b.startDate and b.endDate")
    List<Booking> findAllCurrentBookingByBookerId(@Param("id")Long bookerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = :id " +
            "and CURRENT_TIMESTAMP > b.endDate")
    List<Booking> findAllPastBookingByBookerId(@Param("id")Long bookerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = :id " +
            "and CURRENT_TIMESTAMP < b.startDate")
    List<Booking> findAllFutureBookingByBookerId(@Param("id")Long bookerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.ownerUser.id = :id")
    List<Booking> findAllByOwnerId(@Param("id")Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.ownerUser.id = :id " +
            "and b.status = :status")
    List<Booking> findAllByOwnerIdAndStatus(@Param("id")Long ownerId, @Param("status")BookingStatuses status);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.ownerUser.id = :id " +
            "and CURRENT_TIMESTAMP BETWEEN b.startDate and b.endDate")
    List<Booking> findAllCurrentBookingByOwnerId(@Param("id")Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.ownerUser.id = :id " +
            "and CURRENT_TIMESTAMP > b.endDate")
    List<Booking> findAllPastBookingByOwnerId(@Param("id")Long ownerId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.ownerUser.id = :id " +
            "and CURRENT_TIMESTAMP < b.startDate")
    List<Booking> findAllFutureBookingByOwnerId(@Param("id")Long ownerId);

    @Query("select b.startDate " +
            "from Booking as b " +
            "where b.item.id = :id " +
            "and CURRENT_TIMESTAMP < b.startDate")
    List<LocalDateTime> findNextBookingStartByItemId(@Param("id")Long itemId);

    @Query("select b.endDate " +
            "from Booking as b " +
            "where b.item.id = :id " +
            "and CURRENT_TIMESTAMP > b.endDate ")
    List<LocalDateTime> findLastBookingEndByItemId(@Param("id")Long itemId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (:ids) " +
            "and CURRENT_TIMESTAMP > b.endDate " +
            "order by b.endDate DESC")
    List<Booking> findByItemInAndEndBefore(@Param("ids")List<Long> ids);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (:ids) " +
            "and CURRENT_TIMESTAMP < b.startDate " +
            "order by b.endDate ASC")
    List<Booking> findByItemInAndStartAfter(@Param("ids")List<Long> ids);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "  AND b.item.id = :itemId " +
            "  AND b.endDate < :checkDate")
    Boolean existsByBookerIdAndItemIdAndEndBefore(
            @Param("bookerId") Long bookerId,
            @Param("itemId") Long itemId,
            @Param("checkDate") LocalDateTime checkDate);

}
