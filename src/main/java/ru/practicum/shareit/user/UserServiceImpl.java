package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateExecution;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto addUser(UserCreateDto user) {
        if (existsByEmail(user.getEmail())) {
            throw new DuplicateExecution("Пользователь с email '" + user.getEmail() + "' уже зарегистрирован!");
        }
        return UserMapper.toUserDto(repository.addUser(UserMapper.toUser(user)));
    }

    @Override
    public UserDto patchUser(UserPatchDto patchUser) {
        if (existsById(patchUser.getId())) {
            if (existsByEmail(patchUser.getEmail())) {
                throw new DuplicateExecution("Пользователь с email '" + patchUser.getEmail() + "' уже зарегистрирован!");
            }
            return UserMapper.toUserDto(repository.patchUser(UserMapper.toUser(patchUser)));
        } else {
            throw new ValidationException("Пользователь с id " + patchUser.getId() + " не найден.");
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = repository.getAllUsers();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(repository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден.")));
    }

    @Override
    public void deleteUserById(Long id) {
        repository.deleteUserById(id);
    }

    public boolean existsByEmail(String email) {
        return repository.getAllUsers().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public boolean existsById(Long id) {
        return repository.getUserById(id).isPresent();
    }
}