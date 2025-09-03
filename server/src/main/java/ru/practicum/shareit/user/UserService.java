package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.List;

public interface UserService {
    public UserDto addUser(UserCreateDto user);

    public UserDto patchUser(UserPatchDto patchUser);

    public List<UserDto> getAllUsers();

    public UserDto getUserById(Long id);

    public void deleteUserById(Long id);
}