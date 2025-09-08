package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
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
        if (repository.existsByEmail(user.getEmail())) {
            throw new DuplicateException("Пользователь с email '" + user.getEmail() + "' уже зарегистрирован!");
        }
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(user)));
    }

    @Override
    public UserDto patchUser(UserPatchDto patchUser) {
        User existingUser = repository.findById(patchUser.getId())
                .orElseThrow(() -> new ValidationException("Пользователь с id " + patchUser.getId() + " не найден."));

        if (patchUser.getEmail() != null && repository.existsByEmail(patchUser.getEmail())) {
            throw new DuplicateException("Пользователь с email '" + patchUser.getEmail() + "' уже зарегистрирован!");
        }

        if (patchUser.getName() != null) {
            existingUser.setName(patchUser.getName());
        }
        if (patchUser.getEmail() != null) {
            existingUser.setEmail(patchUser.getEmail());
        }

        return UserMapper.toUserDto(repository.save(existingUser));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден.")));
    }

    @Override
    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }
}