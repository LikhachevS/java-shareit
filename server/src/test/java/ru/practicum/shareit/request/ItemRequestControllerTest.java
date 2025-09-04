package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequestDto request1;
    private ItemRequestDto request2;

    @BeforeEach
    public void setUp() {
        request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("Request 1");
        request1.setCreated(LocalDateTime.now());

        request2 = new ItemRequestDto();
        request2.setId(2L);
        request2.setDescription("Request 2");
        request2.setCreated(LocalDateTime.now());
    }

    @Test
    public void testCreateRequest() throws Exception {
        ItemRequestCreateDto requestCreateDto = new ItemRequestCreateDto();
        requestCreateDto.setDescription("New Request");

        when(itemRequestService.createRequest(any(ItemRequestCreateDto.class))).thenReturn(request1);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Request 1")));
    }

    @Test
    public void testGetOwnRequests() throws Exception {
        when(itemRequestService.getOwnRequests(1L)).thenReturn(Arrays.asList(request1, request2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("Request 1")))
                .andExpect(jsonPath("$[1].description", is("Request 2")));
    }

    @Test
    public void testGetOtherRequests() throws Exception {
        when(itemRequestService.getOtherRequests(1L)).thenReturn(Arrays.asList(request1, request2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("Request 1")))
                .andExpect(jsonPath("$[1].description", is("Request 2")));
    }

    @Test
    public void testGetRequestById() throws Exception {
        when(itemRequestService.getRequestById(1L, 1L)).thenReturn(request1);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Request 1")));
    }
}