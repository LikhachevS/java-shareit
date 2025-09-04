package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    public void setUp() {
        user1 = new UserDto();
        user1.setId(1L);
        user1.setName("User 1");
        user1.setEmail("user1@example.com");

        user2 = new UserDto();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("User 1")))
                .andExpect(jsonPath("$[1].name", is("User 2")));
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user1);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("User 1")));
    }

    @Test
    public void testAddUser() throws Exception {
        // Подготовьте тестовые данные
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("New User");
        userCreateDto.setEmail("newuser@example.com");

        // Настройте моки
        when(userService.addUser(any(UserCreateDto.class))).thenReturn(user1);

        // Выполните запрос
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("User 1")));
    }

    @Test
    public void testPatchUser() throws Exception {
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName("Updated User");
        userPatchDto.setEmail("updateduser@example.com");

        when(userService.patchUser(any(UserPatchDto.class))).thenReturn(user1);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userPatchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("User 1")));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}