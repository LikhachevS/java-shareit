package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(BookingCreateDto bookingCreateDto) {
        System.out.println("Start: " + bookingCreateDto.getStart());
        if (!userRepository.existsById(bookingCreateDto.getBookerId())) {
            throw new NotFoundException("Пользователь с id " + bookingCreateDto.getBookerId() + " не найден");
        }

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + bookingCreateDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Данная вещь не доступна для бронирования");
        }

        return bookingMapper.toBookingDto(bookingRepository.save(bookingMapper.toBooking(bookingCreateDto)));
    }

    @Override
    public BookingDto approveBooking(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        Long ownerId = booking.getItem().getOwner().getId();
        if (!ownerId.equals(userId)) {
            throw new ForbiddenOperationException("Только владелец вещи может утвердить или отклонить бронирование");
        }

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!(userId.equals(ownerId) || userId.equals(bookerId))) {
            throw new ForbiddenOperationException("Доступ запрещён. Просматривать информацию о бронировании может " +
                    "только владелец вещи или инициатор бронирования.");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsForCurrentUser(BookingController.State state, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookings(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatus(userId, BookingStatus.REJECTED);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookings(userId, now);
                break;
            default:
                bookings = bookingRepository.findAllBookings(userId);
                break;
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsForOwnedItems(BookingController.State state, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsForOwner(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsForOwner(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusForOwner(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusForOwner(userId, BookingStatus.REJECTED);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsForOwner(userId, now);
                break;
            default:
                bookings = bookingRepository.findAllBookingsForOwner(userId);
                break;
        }

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}