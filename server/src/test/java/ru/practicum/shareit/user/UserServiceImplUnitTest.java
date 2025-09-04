package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    // Тест на метод добавления пользователя с дублем email
    @Test
    public void testAddUser_ExceptionThrownOnDuplicateEmail() {
        // Подготовка данных: пытаемся добавить пользователя с уже существующим email
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("Сергей Павлович");
        userCreateDto.setEmail("pavlovich.sergey@example.com");

        // Имитация того, что email уже занят
        when(userRepository.existsByEmail(eq("pavlovich.sergey@example.com"))).thenReturn(true);

        // Проверяем, что бросается исключение
        Assertions.assertThrows(DuplicateException.class, () -> {
            userService.addUser(userCreateDto);
        });
    }

    // Тест на метод редактирования пользователя с дублирующим email
    @Test
    public void testPatchUser_ExceptionThrownOnDuplicateEmail() {
        // Подготовка данных: пытаемся изменить email на уже существующий
        User user = new User();
        user.setId(1L);
        user.setName("Алексей Яковлев");
        user.setEmail("yakovlev.aleksey@example.com");
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setId(1L);
        patchDto.setEmail("pavlovich.sergey@example.com");

        // Имитация того, что email уже занят
        when(userRepository.existsByEmail(eq("pavlovich.sergey@example.com"))).thenReturn(true);

        // Проверяем, что бросается исключение
        Assertions.assertThrows(DuplicateException.class, () -> {
            userService.patchUser(patchDto);
        });
    }

    // Тест на метод редактирования пользователя с пустым именем
    @Test
    public void testPatchUser_EmptyNameReplacedWithEmail() {
        // Подготовка данных: пытаемся изменить имя на пустое
        User user = new User();
        user.setId(1L);
        user.setName("Алексей Яковлев");
        user.setEmail("yakovlev.aleksey@example.com");
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setId(1L);
        patchDto.setName(null);
        patchDto.setEmail("yakovlev.aleksey@example.com");

        // Имитация проверки email
        when(userRepository.existsByEmail(eq("yakovlev.aleksey@example.com"))).thenReturn(false);

        // Настройка мока для save
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);

        // 2. Вызов метода редактирования пользователя
        UserDto patchedUser = userService.patchUser(patchDto);

        // 3. Проверка результата
        assertThat(patchedUser.getName()).isEqualTo("Алексей Яковлев");
        assertThat(patchedUser.getEmail()).isEqualTo("yakovlev.aleksey@example.com");
    }
}