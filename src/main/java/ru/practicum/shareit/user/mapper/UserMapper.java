package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserCreateDto userCreateDto) {
        User user = new User();
        if (userCreateDto.getName() != null) {
            user.setName(userCreateDto.getName());
        } else {
            user.setName(userCreateDto.getEmail());
        }
        user.setEmail(userCreateDto.getEmail());
        return user;
    }

    public static User toUser(UserPatchDto userPatchDto) {
        User user = new User();
        user.setId(userPatchDto.getId());
        user.setName(userPatchDto.getName());
        user.setEmail(userPatchDto.getEmail());
        return user;
    }
}