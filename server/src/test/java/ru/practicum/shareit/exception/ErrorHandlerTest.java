package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ItemController.class)
public class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    public void testHandleValidationException() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName(null); // Некорректные данные

        when(itemService.addItem(any(ItemCreateDto.class))).thenThrow(new ValidationException("Некорректные данные"));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Некорректные данные")));
    }

    @Test
    public void testHandleNotFoundException() throws Exception {
        // Настройте моки, чтобы выбрасывать NotFoundException
        when(itemService.getItemById(anyLong(), anyLong())).thenThrow(new NotFoundException("Не найдено"));

        // Выполните запрос
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Не найдено")));
    }

    @Test
    public void testHandleDuplicateException() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Дубликат"); // Некорректные данные

        when(itemService.addItem(any(ItemCreateDto.class))).thenThrow(new DuplicateException("Такой элемент уже существует"));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Такой элемент уже существует")));
    }

    @Test
    public void testHandleForbiddenOperationException() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenThrow(new ForbiddenOperationException("Операция запрещена"));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Операция запрещена")));
    }

    @Test
    public void testHandleThrowable() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenThrow(new RuntimeException("Общая ошибка"));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка.")));
    }
}