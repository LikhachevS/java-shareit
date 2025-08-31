package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {

    private Long bookerId;

    @NotNull(message = "itemId должнен быть указан")
    private Long itemId;

    @NotNull(message = "Начало бронирования не может быть пустым")
    @Future(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Окончание бронирования не может быть пустым")
    @Future(message = "Дата окончания бронирования не может быть в прошлом")
    private LocalDateTime end;

    @AssertTrue(message = "Дата окончания должна быть позже даты начала и не равной ей")
    public boolean isValidPeriod() {
        return end != null && start != null && end.isAfter(start);
    }
}