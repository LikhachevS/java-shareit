package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDto {
    private String name;
    @NotBlank(message = "Email должен быть указан")
    @Email(message = "Некорректный формат Email")
    private String email;
}