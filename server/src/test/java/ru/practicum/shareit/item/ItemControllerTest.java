package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto item1;
    private ItemDto item2;

    @BeforeEach
    public void setUp() {
        item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);

        item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(false);
    }

    @Test
    public void testAddItem() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("New Item");
        itemCreateDto.setDescription("New Description");
        itemCreateDto.setAvailable(true);

        when(itemService.addItem(any(ItemCreateDto.class))).thenReturn(item1);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Item 1")));

    }

    @Test
    public void testPatchItem() throws Exception {
        ItemPatchDto itemPatchDto = new ItemPatchDto();
        itemPatchDto.setName("Updated Item");
        itemPatchDto.setDescription("Updated Description");
        itemPatchDto.setAvailable(false);

        when(itemService.patchItem(any(ItemPatchDto.class))).thenReturn(item1);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemPatchDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("Item 1")));
    }

    @Test
    public void testGetItemById() throws Exception {
        when(itemService.getItemById(1L, 1L)).thenReturn(item1);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("Item 1")));
    }

    @Test
    public void testGetItemsFromUser() throws Exception {
        when(itemService.getItemsFromUser(1L)).thenReturn(Arrays.asList(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Item 1")))
                .andExpect(jsonPath("$[1].name", Matchers.is("Item 2")));
    }

    @Test
    public void testSearchItems() throws Exception {
        // Настройте моки
        when(itemService.searchItems("Item")).thenReturn(Arrays.asList(item1, item2));

        // Выполните запрос
        mockMvc.perform(get("/items/search?text=Item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[1].name", is("Item 2")));
    }

    @Test
    public void testAddComment() throws Exception {
        // Подготовьте тестовые данные
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Test Comment");

        // Настройте моки
        when(itemService.addComment(any(CommentCreateDto.class))).thenReturn(new CommentDto());

        // Выполните запрос
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }
}