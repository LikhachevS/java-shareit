package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserPatchDto {
    private Long id;
    private String name;
    @Email(message = "Некорректный формат Email")
    private String email;
}