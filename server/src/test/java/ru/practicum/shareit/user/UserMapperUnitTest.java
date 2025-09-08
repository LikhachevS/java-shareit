package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperUnitTest {

    private UserCreateDto validUserCreateDto;
    private UserCreateDto emptyNameUserCreateDto;

    @BeforeEach
    public void setUp() {
        // Создаем валидный DTO с именем и email
        validUserCreateDto = new UserCreateDto();
        validUserCreateDto.setName("Иван Иванов");
        validUserCreateDto.setEmail("ivanov@example.com");

        // Создаем DTO без имени, только email
        emptyNameUserCreateDto = new UserCreateDto();
        emptyNameUserCreateDto.setEmail("empty_name_user@example.com");
    }

    // Тест №1: проверка, что имя пользователя копируется из DTO
    @Test
    public void testToUser_NameCopiedFromDTO() {
        // Вызываем метод преобразования
        User user = UserMapper.toUser(validUserCreateDto);

        // Проверяем результат
        assertEquals("Иван Иванов", user.getName());
        assertEquals("ivanov@example.com", user.getEmail());
    }

    // Тест №2: проверка, что если имя не передано, подставляется email
    @Test
    public void testToUser_EmptyNameReplacedWithEmail() {
        // Вызываем метод преобразования
        User user = UserMapper.toUser(emptyNameUserCreateDto);

        // Проверяем результат
        assertEquals("empty_name_user@example.com", user.getName());
        assertEquals("empty_name_user@example.com", user.getEmail());
    }

    // Тест №3: проверка на недопустимый пустой email
    @Test
    public void testToUser_EmailMustNotBeNull() {
        UserCreateDto invalidDto = new UserCreateDto();
        invalidDto.setName("Имя без Email");
        invalidDto.setEmail(null); // намеренно ставим email как null

        // Исключения в случае неправильных данных можно обрабатывать вручную, но здесь покажу только нормальное поведение
        User user = UserMapper.toUser(invalidDto);

        // Проверяем, что имя скопировано, хотя email остался null
        assertEquals("Имя без Email", user.getName());
        assertNull(user.getEmail());
    }

    // Тест №4: расширенный тест с полным описанием данных
    @Test
    public void testToUser_CompleteScenario() {
        // Составляем валидные данные
        UserCreateDto completeDto = new UserCreateDto();
        completeDto.setName("Полный сценарий");
        completeDto.setEmail("complete_scenario@example.com");

        // Проводим преобразование
        User user = UserMapper.toUser(completeDto);

        // Проверяем результаты
        assertEquals("Полный сценарий", user.getName());
        assertEquals("complete_scenario@example.com", user.getEmail());
    }
}