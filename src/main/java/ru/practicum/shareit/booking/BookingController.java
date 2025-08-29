package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;
    private static final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestBody @Valid BookingCreateDto bookingDto,
                                    @RequestHeader(xSharerUserId) long userId) {
        bookingDto.setBookerId(userId);
        return service.createBooking(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader(xSharerUserId) long userId) {
        return service.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(xSharerUserId) long userId) {
        return service.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsForCurrentUser(@RequestParam(defaultValue = "ALL") State state,
                                                      @RequestHeader(xSharerUserId) long userId) {
        return service.getBookingsForCurrentUser(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForOwnedItems(@RequestParam(defaultValue = "ALL") State state,
                                                     @RequestHeader(xSharerUserId) long userId) {
        return service.getBookingsForOwnedItems(state, userId);
    }

    public enum State {
        ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED
    }
}