package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemRequestCreateDto {
    private Long requesterId;
    private String description;
}
