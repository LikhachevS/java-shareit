package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentCreateDto {
    private Long itemId;

    private Long authorId;

    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}