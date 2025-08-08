package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User addUser(User user);

    User patchUser(User newUser);

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    void deleteUserById(Long id);
}
