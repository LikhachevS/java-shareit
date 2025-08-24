package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private Long nextId;
    private final List<User> users;

    public InMemoryUserRepository() {
        users = new ArrayList<>();
        nextId = 1L;
    }

    @Override
    public User addUser(User user) {
        user.setId(nextId);
        nextId++;
        users.add(user);
        return user;
    }

    @Override
    public User patchUser(User patchUser) {
        User existingUser = getUserById(patchUser.getId()).get();
        if (patchUser.getName() != null) {
            existingUser.setName(patchUser.getName());
        }
        if (patchUser.getEmail() != null) {
            existingUser.setEmail(patchUser.getEmail());
        }
        return existingUser;
    }

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public void deleteUserById(Long id) {
        users.removeIf(user -> user.getId().equals(id));
    }
}