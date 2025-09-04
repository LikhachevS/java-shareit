package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplUnitTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Test
    public void testCreateBooking_UserNotFound() {
        // Подготовка тестовых данных
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setBookerId(1L);
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(1L))).thenReturn(false);

        // Ожидаем исключение
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingCreateDto);
        });
    }

    @Test
    public void testCreateBooking_ItemNotFound() {
        // Подготовка тестовых данных
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setBookerId(1L);
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(1L))).thenReturn(true);

        // Имитация отсутствия вещи
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Ожидаем исключение
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookingCreateDto);
        });
    }

    @Test
    public void testCreateBooking_ItemNotAvailable() {
        // Подготовка тестовых данных
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setBookerId(1L);
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(1L))).thenReturn(true);

        // Имитация получения недоступной вещи
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(false);

        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item));

        // Ожидаем исключение
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(bookingCreateDto);
        });
    }

    @Test
    public void testApproveBooking_ForbiddenOperation() {
        // Подготовка тестовых данных
        Long bookingId = 1L;
        Long userId = 1L;
        boolean approved = true;

        // Имитация получения бронирования
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(new Item());
        booking.getItem().setOwner(new User());
        booking.getItem().getOwner().setId(2L); // Другой пользователь

        when(bookingRepository.findById(eq(bookingId))).thenReturn(Optional.of(booking));

        // Ожидаем исключение
        Assertions.assertThrows(ForbiddenOperationException.class, () -> {
            bookingService.approveBooking(bookingId, approved, userId);
        });
    }

    @Test
    public void testGetBooking_ForbiddenOperation() {
        // Подготовка тестовых данных
        Long bookingId = 1L;
        Long userId = 1L;

        // Имитация получения бронирования
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(new Item());
        booking.getItem().setOwner(new User());
        booking.getItem().getOwner().setId(2L); // Другой пользователь
        booking.setBooker(new User());
        booking.getBooker().setId(3L); // Другой пользователь

        when(bookingRepository.findById(eq(bookingId))).thenReturn(Optional.of(booking));

        // Ожидаем исключение
        Assertions.assertThrows(ForbiddenOperationException.class, () -> {
            bookingService.getBooking(bookingId, userId);
        });
    }

    @Test
    public void testGetBookingsForCurrentUser_UserNotFound() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.CURRENT;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(false);

        // Ожидаем исключение
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsForCurrentUser(state, userId);
        });
    }

    @Test
    public void testGetBookingsForCurrentUser_FutureBookings() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.FUTURE;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация получения будущих бронирований
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingRepository.findFutureBookings(eq(userId), any(LocalDateTime.class))).thenReturn(bookings);

        // Имитация маппинга в DTO
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(new BookingDto());
        bookingDtos.add(new BookingDto());

        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        // Вызов метода
        List<BookingDto> result = bookingService.getBookingsForCurrentUser(state, userId);

        // Проверка результата
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testGetBookingsForCurrentUser_WaitingBookings() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.WAITING;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация получения ожидающих бронирований
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingRepository.findBookingsByStatus(eq(userId), eq(BookingStatus.WAITING))).thenReturn(bookings);

        // Имитация маппинга в DTO
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(new BookingDto());
        bookingDtos.add(new BookingDto());

        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        // Вызов метода
        List<BookingDto> result = bookingService.getBookingsForCurrentUser(state, userId);

        // Проверка результата
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testGetBookingsForCurrentUser_RejectedBookings() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.REJECTED;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация получения отклоненных бронирований
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingRepository.findBookingsByStatus(eq(userId), eq(BookingStatus.REJECTED))).thenReturn(bookings);

        // Имитация маппинга в DTO
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(new BookingDto());
        bookingDtos.add(new BookingDto());

        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        // Вызов метода
        List<BookingDto> result = bookingService.getBookingsForCurrentUser(state, userId);

        // Проверка результата
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testGetBookingsForCurrentUser_PastBookings() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.PAST;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация получения прошлых бронирований
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingRepository.findPastBookings(eq(userId), any(LocalDateTime.class))).thenReturn(bookings);

        // Имитация маппинга в DTO
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(new BookingDto());
        bookingDtos.add(new BookingDto());

        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        // Вызов метода
        List<BookingDto> result = bookingService.getBookingsForCurrentUser(state, userId);

        // Проверка результата
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testGetBookingsForCurrentUser_AllBookings() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.ALL;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация получения всех бронирований
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingRepository.findAllBookings(eq(userId))).thenReturn(bookings);

        // Имитация маппинга в DTO
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(new BookingDto());
        bookingDtos.add(new BookingDto());

        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        // Вызов метода
        List<BookingDto> result = bookingService.getBookingsForCurrentUser(state, userId);

        // Проверка результата
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testGetBookingsForOwnedItems_UserNotFound() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.CURRENT;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(false);

        // Ожидаем исключение
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsForOwnedItems(state, userId);
        });
    }

    @Test
    public void testGetBookingsForOwnedItems_SuccessfulRetrieval() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.CURRENT;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация получения бронирований
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingRepository.findCurrentBookingsForOwner(eq(userId), any(LocalDateTime.class))).thenReturn(bookings);

        // Имитация маппинга в DTO
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(new BookingDto());
        bookingDtos.add(new BookingDto());

        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        // Вызов метода
        List<BookingDto> result = bookingService.getBookingsForOwnedItems(state, userId);

        // Проверка результата
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testGetBookingsForOwnedItems_FutureBookings() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.FUTURE;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация получения будущих бронирований
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingRepository.findFutureBookingsForOwner(eq(userId), any(LocalDateTime.class))).thenReturn(bookings);

        // Имитация маппинга в DTO
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(new BookingDto());
        bookingDtos.add(new BookingDto());

        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        // Вызов метода
        List<BookingDto> result = bookingService.getBookingsForOwnedItems(state, userId);

        // Проверка результата
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testGetBookingsForOwnedItems_WaitingBookings() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.WAITING;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация получения ожидающих бронирований
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingRepository.findBookingsByStatusForOwner(eq(userId), eq(BookingStatus.WAITING))).thenReturn(bookings);

        // Имитация маппинга в DTO
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(new BookingDto());
        bookingDtos.add(new BookingDto());

        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        // Вызов метода
        List<BookingDto> result = bookingService.getBookingsForOwnedItems(state, userId);

        // Проверка результата
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testGetBookingsForOwnedItems_RejectedBookings() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.REJECTED;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация получения отклоненных бронирований
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingRepository.findBookingsByStatusForOwner(eq(userId), eq(BookingStatus.REJECTED))).thenReturn(bookings);

        // Имитация маппинга в DTO
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(new BookingDto());
        bookingDtos.add(new BookingDto());

        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        // Вызов метода
        List<BookingDto> result = bookingService.getBookingsForOwnedItems(state, userId);

        // Проверка результата
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testGetBookingsForOwnedItems_PastBookings() {
        // Подготовка тестовых данных
        Long userId = 1L;
        BookingController.State state = BookingController.State.PAST;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация получения прошлых бронирований
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingRepository.findPastBookingsForOwner(eq(userId), any(LocalDateTime.class))).thenReturn(bookings);

        // Имитация маппинга в DTO
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(new BookingDto());
        bookingDtos.add(new BookingDto());

        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        // Вызов метода
        List<BookingDto> result = bookingService.getBookingsForOwnedItems(state, userId);

        // Проверка результата
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
    }
}