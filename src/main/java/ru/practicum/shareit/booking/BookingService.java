package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingCreateDto bookingDto);

    BookingDto approveBooking(Long bookingId, boolean approved, Long userId);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getBookingsForCurrentUser(BookingController.State state, Long userId);

    List<BookingDto> getBookingsForOwnedItems(BookingController.State state, Long userId);
}