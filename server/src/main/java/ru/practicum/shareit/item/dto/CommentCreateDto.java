package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class CommentCreateDto {
    private Long itemId;
    private Long authorId;
    private String text;
}