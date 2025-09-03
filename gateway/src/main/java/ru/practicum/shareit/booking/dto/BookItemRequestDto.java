package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull(message = "itemId должен быть указан")
    private long itemId;

    @NotNull(message = "Начало бронирования не может быть пустым")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Окончание бронирования не может быть пустым")
    @Future(message = "Дата окончания бронирования не может быть в прошлом")
    private LocalDateTime end;

    public boolean isValidPeriod() {
        return end != null && start != null && end.isAfter(start);
    }
}
