package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplUnitTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestMapper requestMapper;

    @Test
    public void testCreateRequest_SuccessfulCreation() {
        // Подготовка тестовых данных
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setRequesterId(1L);
        itemRequestCreateDto.setDescription("Test request");

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(1L))).thenReturn(true);

        // Имитация маппинга и сохранения запроса
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(new User());

        when(requestMapper.toItemRequest(eq(itemRequestCreateDto), any(LocalDateTime.class))).thenReturn(itemRequest);
        when(itemRequestRepository.save(eq(itemRequest))).thenReturn(itemRequest);

        // Имитация маппинга в DTO
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Test request");
        itemRequestDto.setCreated(LocalDateTime.now());

        when(requestMapper.toItemRequestDto(eq(itemRequest))).thenReturn(itemRequestDto);

        // Вызов метода
        ItemRequestDto createdRequest = itemRequestService.createRequest(itemRequestCreateDto);

        // Проверка результата
        assertThat(createdRequest.getId()).isEqualTo(1L);
        assertThat(createdRequest.getDescription()).isEqualTo("Test request");
        assertThat(createdRequest.getCreated()).isNotNull();
    }

    @Test
    public void testCreateRequest_UserNotFound() {
        // Подготовка тестовых данных
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setRequesterId(1L);
        itemRequestCreateDto.setDescription("Test request");

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(1L))).thenReturn(false);

        // Ожидаем исключение
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.createRequest(itemRequestCreateDto);
        });
    }

    @Test
    public void testGetOwnRequests_UserNotFound() {
        // Подготовка тестовых данных
        long userId = 1L;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(false);

        // Ожидаем исключение
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getOwnRequests(userId);
        });
    }

    @Test
    public void testGetOtherRequests_UserNotFound() {
        // Подготовка тестовых данных
        long userId = 1L;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(false);

        // Ожидаем исключение
        Assertions.assertThrows(ForbiddenOperationException.class, () -> {
            itemRequestService.getOtherRequests(userId);
        });
    }

    @Test
    public void testGetRequestById_UserNotFound() {
        // Подготовка тестовых данных
        long userId = 1L;
        long requestId = 1L;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(false);

        // Ожидаем исключение
        Assertions.assertThrows(ForbiddenOperationException.class, () -> {
            itemRequestService.getRequestById(requestId, userId);
        });
    }

    @Test
    public void testGetRequestById_RequestNotFound() {
        // Подготовка тестовых данных
        long userId = 1L;
        long requestId = 1L;

        // Имитация проверки существования пользователя
        when(userRepository.existsById(eq(userId))).thenReturn(true);

        // Имитация отсутствия запроса
        when(itemRequestRepository.findById(eq(requestId))).thenReturn(Optional.empty());

        // Ожидаем исключение
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(requestId, userId);
        });
    }
}