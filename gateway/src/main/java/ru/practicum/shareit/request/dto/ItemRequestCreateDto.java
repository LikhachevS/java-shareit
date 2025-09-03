package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestCreateDto {
    @NotBlank(message = "Текст запроса не может быть пустым")
    private String description;
}
