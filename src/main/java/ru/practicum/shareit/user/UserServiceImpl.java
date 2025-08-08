package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateExecution;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User addUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new DuplicateExecution("Пользователь с email '" + user.getEmail() + "' уже зарегистрирован!");
        }
        return repository.addUser(user);
    }

    @Override
    public User patchUser(User patchUser) {
        if (existsById(patchUser.getId())) {
            if (existsByEmail(patchUser.getEmail())) {
                throw new DuplicateExecution("Пользователь с email '" + patchUser.getEmail() + "' уже зарегистрирован!");
            }
            return repository.patchUser(patchUser);
        } else {
            throw new ValidationException("Пользователь с id " + patchUser.getId() + " не найден.");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return repository.getAllUsers();
    }

    @Override
    public User getUserById(Long id) {
        return repository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
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