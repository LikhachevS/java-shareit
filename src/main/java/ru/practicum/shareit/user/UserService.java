package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    public User addUser(User user);

    public User patchUser(User patchUser);

    public List<User> getAllUsers();

    public User getUserById(Long id);

    public void deleteUserById(Long id);
}
