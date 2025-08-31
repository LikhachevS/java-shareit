package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start <= :now AND b.end > :now ORDER BY b.start DESC")
    List<Booking> findCurrentBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingsByStatus(@Param("userId") Long userId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    List<Booking> findAllBookings(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start <= :now AND b.end > :now ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsForOwner(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureBookingsForOwner(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastBookingsForOwner(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingsByStatusForOwner(@Param("userId") Long userId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId ORDER BY b.start DESC")
    List<Booking> findAllBookingsForOwner(@Param("userId") Long userId);

    boolean existsByBookerIdAndItemIdAndEndBefore(Long authorId, Long itemId, LocalDateTime now);

    @Query("SELECT b.end FROM Booking b WHERE b.item.id = :itemId AND b.end <= :now ORDER BY b.end DESC LIMIT 1")
    Optional<LocalDateTime> findLastBookingDate(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b.start FROM Booking b WHERE b.item.id = :itemId AND b.start >= :now ORDER BY b.start ASC LIMIT 1")
    Optional<LocalDateTime> findNextBookingDate(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);
}